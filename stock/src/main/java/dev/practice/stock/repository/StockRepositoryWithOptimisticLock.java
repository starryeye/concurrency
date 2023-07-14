package dev.practice.stock.repository;

import dev.practice.stock.domain.StockWithVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepositoryWithOptimisticLock extends JpaRepository<StockWithVersion, Long> {
}
