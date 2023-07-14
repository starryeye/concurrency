package dev.practice.stock.service;

import dev.practice.stock.domain.Stock;
import dev.practice.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 참고
 * - synchronized 키워드는 단일 JVM 에서만 동작하므로 분산 환경에서는 여전히 동시성 문제가 발생한다.
 */
@Service
@RequiredArgsConstructor
public class StockServiceWithSynchronized {

    private final StockRepository stockRepository;

    public synchronized void decreaseStock(Long id, Long quantity) {
        /**
         * 1. 재고 조회
         * 2. 재고 업데이트
         * 3. 재고 저장
         */
        Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }
}
