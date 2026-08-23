package com.orderflow.domain.entity;

import java.time.LocalDateTime;

import com.orderflow.domain.enums.OutboxEventStatus;
import com.orderflow.domain.enums.OutboxEventType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxEventStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private int publishAttempts;

    @Column(length = 1000)
    private String lastError;

    protected OutboxEvent() {
    }

    public OutboxEvent(Long aggregateId, OutboxEventType eventType) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.status = OutboxEventStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getAggregateId() {
        return aggregateId;
    }

    public OutboxEventType getEventType() {
        return eventType;
    }

    public OutboxEventStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public int getPublishAttempts() {
        return publishAttempts;
    }

    public String getLastError() {
        return lastError;
    }

    public void markPublished() {
        this.status = OutboxEventStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        this.publishAttempts++;
        this.lastError = null;
    }

    public void recordPublishFailure(String error) {
        this.publishAttempts++;
        this.lastError = error == null ? null : error.substring(0, Math.min(error.length(), 1000));
    }
}
