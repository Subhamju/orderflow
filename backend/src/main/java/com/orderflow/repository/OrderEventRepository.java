package com.orderflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderflow.domain.entity.OrderEvent;
import java.util.List;

public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {

    List<OrderEvent> findByOrderIdOrderByCreatedAtAsc(Long orderId);

}
