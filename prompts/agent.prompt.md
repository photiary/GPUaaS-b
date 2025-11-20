# 🧩 Redis Stream

- 리소스 사용률 메트릭
- Job 과 Container 상태 모니터링

## Redis Stream Key

| 항목 | 예시 | 설명 |  |
| --- | --- | --- | --- |
| Job 단위 리소스 사용률 메트릭 | `metrics:job:{jobId}`  | Job별 GPU/CPU 등 리소스 사용률 스트림 |  |
| Job 단위  App Container 상태 | `state:job:{jobId}` | Job별  App Container 상태를 스트림 |  |
| Job 상태 | `state:jobs:agg` | 모든 Job 상태를 스트림 |  |

# 🧩 리소스 사용률 메트릭

## Stream 데이터 구조

```json
XADD metrics:job:123 * data='{
	"jobId": "123", 
  "containers": [
    {
      "timestamp": "YYYY-MM-DDThh:mm:ssZ",
      "containerId": "abc",
      "cpuUsage": 32.4,
      "gpuUsage": 11.8,
      "memoryUsage": 65.2,
    },
    {
      "timestamp": "YYYY-MM-DDThh:mm:ssZ",
      "containerId": "def",
      "cpuUsage": 28.1,
      "gpuUsage": 9.3,
      "memoryUsage": 71.5,
    },
  ]
}'
```

## Class Diagram

```mermaid
classDiagram
class MetricsCollectorAgent {
  +start(jobId: String)
  +stop(jobId: String)
  -scheduleCollection()
  -collectAll(containerId: String)
}

class Collector {
  <<interface>>
  +collect(containerId: String): MetricsData
}

class SystemCollector {
  +collect(containerId: String): MetricsData
}

class GPUCollector {
  +collect(containerId: String): MetricsData
}

class Aggregator {
  +aggregate(dataList: List<MetricsData>): MetricsData
}

class Transporter~T~ {
  <<interface>>
  +send(jobId: String, data: T)
}

class RedisStreamTransporter~T~ {
  +streamKeyPrefix: String
  +send(jobId: String, data: T)
  -buildStreamKey(jobId: String): String
  -serialize(data: T): String
}

class MetricsData {
  +containerId: String
  +timestamp: Instant
  +cpuUsage: double
  +gpuUsage: double
  +memoryUsage: double
  +toJson(): String
}

%% 관계 정의
MetricsCollectorAgent --> Collector : uses
MetricsCollectorAgent --> Aggregator : aggregates
MetricsCollectorAgent --> Transporter~MetricsData~ : sends via
Collector <|.. SystemCollector
Collector <|.. GPUCollector
Transporter <|.. RedisStreamTransporter
Aggregator --> MetricsData
RedisStreamTransporter --> MetricsData
MetricsCollectorAgent --> MetricsData

```

## Sequence

```mermaid
sequenceDiagram
    participant Scheduler as Spring Scheduler (@Scheduled)
    participant Agent as MetricsCollectorAgent
    participant ContainerRepo as AppContainerNodeRepository
    participant Sys as SystemCollector
    participant Gpu as GPUCollector
    participant Agg as Aggregator
    participant Trans as RedisStreamTransporter
    participant Redis as Redis Stream (metrics:job:{jobId})

    Scheduler->>Agent: start(jobId) 호출
    Agent->>ContainerRepo: findContainersByJobId(jobId)
    ContainerRepo-->>Agent: List<containerId>

    loop 각 containerId 별
        Agent->>Sys: collect(containerId)
        Sys-->>Agent: MetricsData(cpu, memory)
        Agent->>Gpu: collect(containerId)
        Gpu-->>Agent: MetricsData(gpu)
        Agent->>Agg: aggregate(containerId, [MetricsData...])
        Agg-->>Agent: AggregatedMetrics(containerId)
    end

    Note right of Agent: container별 MetricsData를<br/>Job 단위 데이터 취합

    Agent->>Trans: send(jobId, List<AggregatedMetrics>)
    Trans->>Redis: XADD metrics:job:{jobId} data={containers:[...], jobId}
    Redis-->>Trans: RecordId 응답
    Trans-->>Agent: 전송 완료

```

# 🧩 Job & Container 상태

## Stream 데이터 구조

```json
XADD state:job:123 * data='{
			"jobId": "123", 
		  "containers": [
		    {
		      "timestamp": "YYYY-MM-DDThh:mm:ssZ",
		      "containerId": "abc",
		      "status": "COMPLETED"
		      "startTime": "YYYY-MM-DDThh:mm:ssZ",
		      "endTime": "YYYY-MM-DDThh:mm:ssZ"
		    },
		    {
		      "timestamp": "YYYY-MM-DDThh:mm:ssZ",
		      "containerId": "def",
		      "status": "RUNNING"
		      "startTime": "YYYY-MM-DDThh:mm:ssZ",
		      "endTime": "YYYY-MM-DDThh:mm:ssZ"
		    },
		  ]
}'
```

## Class Diagram

```mermaid
classDiagram
class StateMonitorAgent {
  +startAll()
  +stopAll()
  -scheduleAllJobs()
  -getActiveJobIds(): List<String>
  -dispatchJobMonitoring(jobId: String)
}

class AppStateMonitorAgent {
  +start(jobId: String)
  +stop(jobId: String)
  -scheduleCollection()
  -collectAll(containerId: String)
}

class StateCollector {
  <<interface>>
  +collect(containerId: String): ContainerStateData
}

class AppContainerStateCollector {
  +collect(containerId: String): ContainerStateData
}

class Transporter~T~ {
  <<interface>>
  +send(jobId: String, data: T)
}

class RedisStreamTransporter~T~ {
  +streamKeyPrefix: String
  +send(jobId: String, data: T)
  -buildStreamKey(jobId: String): String
  -serialize(data: T): String
}

class ContainerStateData {
  +containerId: String
  +state: String  // RUNNING, STOPPED, FAILED 등
  +timestamp: Instant
  +toJson(): String
}

class JobStateData {
  +jobId: String
  +containers: List<ContainerStateData>
  +timestamp: Instant
  +toJson(): String
}

%% 관계 정의
StateMonitorAgent --> AppStateMonitorAgent : manages job agents
AppStateMonitorAgent --> StateCollector : uses
AppStateMonitorAgent --> Transporter~JobStateData~ : sends via
StateCollector <|.. AppContainerStateCollector
Transporter <|.. RedisStreamTransporter
AppStateMonitorAgent --> ContainerStateData
AppStateMonitorAgent --> JobStateData
RedisStreamTransporter --> JobStateData
RedisStreamTransporter --> ContainerStateData

```

## Sequence

```mermaid
sequenceDiagram
    autonumber
    participant Scheduler as Spring Scheduler (@Scheduled)
    participant StateMgr as StateMonitorAgent
    participant AppAgent as AppStateMonitorAgent
    participant Collector as AppContainerStateCollector
    participant Trans as RedisStateTransporter
    participant Redis as Redis Stream (metrics:job)

    Note over StateMgr: 🔁 StateMonitorAgent는 주기적으로<br/>전체 Job 상태를 점검한다.

    Scheduler->>StateMgr: scheduleAllJobs() 호출
    StateMgr->>StateMgr: getActiveJobIds()
    StateMgr-->>Scheduler: [jobId1, jobId2, ...]

    loop 각 Job ID별
        StateMgr->>StateMgr: dispatchJobMonitoring(jobId)
        StateMgr->>AppAgent: start(jobId)
        Note over AppAgent: AppStateMonitorAgent가<br/>해당 Job의 모든 Container 상태를 수집 시작

        AppAgent->>AppAgent: scheduleCollection() (주기 실행)
        loop 각 Container ID별
            AppAgent->>Collector: collect(containerId)
            Collector-->>AppAgent: ContainerStateData(containerId, state, timestamp)
        end

        AppAgent->>AppAgent: JobStateData 구성<br/>(jobId, containers[...])
        AppAgent->>Trans: send(jobId, JobStateData)
        Trans->>Redis: XADD metrics:job:{jobId} data=<JobStateData JSON>
        Redis-->>Trans: RecordId 응답
        Trans-->>AppAgent: 전송 완료
    end

    Note over Redis: 🧩 Redis Stream 예시 키:<br/>metrics:job:{jobId}<br/>data={ jobId, containers: [...] }

    Note over Client,Redis: 📈 대시보드나 SSE API는<br/>metrics:job Stream을 구독하여 Job별<br/>Container 상태를 실시간 표시 가능

```

# 🧩 추가 Class Diagram

```mermaid
classDiagram
class Transporter~T~ {
  <<interface>>
  +send(jobId: String, data: T)
}

class RedisStreamTransporter~T~ {
  +streamKeyPrefix: String
  +send(jobId: String, data: T)
  -buildStreamKey(jobId: String): String
  -serialize(data: T): String
}

class MetricsData {
  +containerId: String
  +timestamp: Instant
  +cpuUsage: double
  +gpuUsage: double
  +memoryUsage: double
  +toJson(): String
}

class JobStateData {
  +jobId: String
  +containers: List<ContainerStateData>
  +timestamp: Instant
  +toJson(): String
}

class MetricsCollectorAgent {
  +start(jobId: String)
  +stop(jobId: String)
  -collectAll(containerId: String)
  -aggregate()
}

class AppStateMonitorAgent {
  +start(jobId: String)
  +stop(jobId: String)
  -collectAll(containerId: String)
}

Transporter <|.. RedisStreamTransporter
MetricsCollectorAgent --> RedisStreamTransporter : uses (MetricsData)
AppStateMonitorAgent --> RedisStreamTransporter : uses (JobStateData)
RedisStreamTransporter --> MetricsData
RedisStreamTransporter --> JobStateData

```