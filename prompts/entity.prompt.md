# 🧩 Job

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `id` | UUID | Job 식별자 |
| `name` | String | Job 이름 |
| `description` | TEXT | Job 설명 |
| `status` | ENUM(`QUEUED`, `RUNNING`, `COMPLETED`, `FAILED`, `STOPPED`) | 현재 상태 |
| `submit_time` | DATETIME | 제출 시각(시작 대기열 진입 시점) |
| `start_time` | DATETIME | 실행 시작 시각 |
| `end_time` | DATETIME | 완료 시각 |
| `requested_gpus` | INT | GPU 요청 수 |
| `requested_cpus` | INT | CPU 요청 수 |
| `requested_memory` | INT | 메모리 요청량 (MB 단위) |
| `metadata` | JSON | ReactFlow 그래프 관련 메타데이터 (줌, 뷰포트 등) |

# 🧩 Resource Node (CPU, GPU 등 자원)

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `id` | UUID | Node 식별자 |
| `name` | VARCHAR | Node 이름 |
| `type` | ENUM(`GPU`, `CPU`) | 노드 타입 |
| `gpu_count` | INT | GPU 수량 |
| `cpu_cores` | INT | CPU 코어 수 |
| `memory_capacity` | INT | 총 메모리 (MB) |
| `status` | ENUM(`ONLINE`, `OFFLINE`, `MAINTENANCE`) | 현재 노드 상태 |
| `last_heartbeat` | DATETIME | 마지막 Heartbeat 시간 |

# 🧩 App Container Node (실행 어플리케이션)

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `id` | UUID | 컨테이너 식별자 |
| `job_id` | UUID (FK → Job) | 실행 중인 Job |
| `node_id` | UUID (FK → Node) | 실행된 노드 |
| `label` | VARCHAR | 컨테이너 이름 |
| `description` | TEXT | 컨테이너 설명 |
| `sequence` | INT | 실행 순서 (CHAIN 모드에서 순차 실행 시 사용) |
| `status` | ENUM(`CREATED`, `WAITING`, `RUNNING`, `COMPLETED`, `FAILED`, `STOPPED`) | 실행 상태 |
| `config` | JSON | 환경 변수, 포트, 리소스 등 설정 |
| `position_x` | FLOAT | ReactFlow X 좌표 |
| `position_y` | FLOAT | ReactFlow Y 좌표 |
| `start_time` | DATETIME | 실행 시작 시간 |
| `end_time` | DATETIME | 종료 시간 |

# 🧩 App Container Edge

| 필드 | 타입 | 설명                                  |
| --- | --- |-------------------------------------|
| `id` | UUID | 엣지 식별자                              |
| `job_id` | UUID (FK → Job) | 상위 Job                              |
| `source_container_id` | UUID (FK → App Container Node) | 시작 컨테이너 노드                          |
| `target_container_id` | UUID (FK → App Container Node) | 도착 컨테이너 노드                          |
| `edge_key` | VARCHAR | ReactFlow용 ID (ex: `"edge-1-2"`)    |
| `label` | VARCHAR | Edge에 표시할 설명 (ex: "output → input") |
| `condition` | JSON | 실행 조건 (예: 성공 시만 실행, 특정 변수 등)        |
| `is_active` | BOOLEAN | 활성/비활성 여부                           |