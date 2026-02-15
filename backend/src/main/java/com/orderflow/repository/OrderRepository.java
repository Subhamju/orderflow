package com.orderflow.repository;

import com.orderflow.domain.entity.Order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUserIdAndIdempotencyKey(
            Long userId,
            String idempotencyKey);

    @Query("""
            SELECT o FROM Order o
            WHERE o.instrumentId = :instrumentId
            AND o.orderType = 'SELL'
            AND o.orderStatus IN ('SENT_TO_EXECUTOR','PARTIALLY_FILLED')
            AND o.remainingQuantity > 0
            AND (:price IS NULL OR o.price <= :price)
            ORDER BY o.price ASC, o.createdAt ASC
            """)
    List<Order> findMatchingSellOrders(Long instrumentId, Double price);

    @Query("""
            SELECT o FROM Order o
            WHERE o.instrumentId = :instrumentId
            AND o.orderType = 'BUY'
            AND o.orderStatus IN ('SENT_TO_EXECUTOR','PARTIALLY_FILLED')
            AND o.remainingQuantity > 0
            AND (:price IS NULL OR o.price >= :price)
            ORDER BY o.price DESC, o.createdAt ASC
            """)
    List<Order> findMatchingBuyOrders(Long instrumentId, Double price);

}
