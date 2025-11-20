package com.funa.common.transport;

/**
 * Generic transporter interface for sending data to an external system.
 */
public interface Transporter<T> {
    void send(String jobId, T data);
}
