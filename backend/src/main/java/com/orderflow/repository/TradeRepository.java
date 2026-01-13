package com.orderflow.repository;

import com.orderflow.domain.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade,Long> {
}
