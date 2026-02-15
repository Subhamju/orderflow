package com.orderflow.domain.statemachine;

import com.orderflow.domain.enums.OrderStatus;

import java.util.Map;
import java.util.Set;

public final class OrderStateMachine {
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(OrderStatus.CREATED,
            Set.of(OrderStatus.VALIDATED, OrderStatus.CANCELLED),
            OrderStatus.VALIDATED, Set.of(OrderStatus.SENT_TO_EXECUTOR, OrderStatus.REJECTED, OrderStatus.CANCELLED),
            OrderStatus.SENT_TO_EXECUTOR, Set.of(OrderStatus.EXECUTING, OrderStatus.CANCELLED),
            OrderStatus.EXECUTING,
            Set.of(OrderStatus.EXECUTED, OrderStatus.PARTIALLY_FILLED, OrderStatus.FAILED, OrderStatus.CANCELLED),
            OrderStatus.PARTIALLY_FILLED,
            Set.of(OrderStatus.PARTIALLY_FILLED, OrderStatus.EXECUTED, OrderStatus.FAILED,
                    OrderStatus.PARTIALLY_CANCELLED),
            OrderStatus.EXECUTED, Set.of(),
            OrderStatus.PARTIALLY_CANCELLED, Set.of(),
            OrderStatus.CANCELLED, Set.of(),
            OrderStatus.FAILED, Set.of(),
            OrderStatus.REJECTED, Set.of());

    private OrderStateMachine() {
    }

    public static void validateState(OrderStatus current, OrderStatus next) {

        if (current == null && next == OrderStatus.CREATED) {
            return;
        }

        if (!ALLOWED_TRANSITIONS
                .getOrDefault(current, Set.of())
                .contains(next)) {
            throw new IllegalStateException("Invalid order state transition: " + current + " → " + next);
        }
    }
}
