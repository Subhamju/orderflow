package com.orderflow.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.orderflow.domain.entity.OutboxEvent;
import com.orderflow.domain.enums.OutboxEventStatus;

import jakarta.persistence.LockModeType;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxEventStatus status, Pageable pageable);
}
