package dev.practice.stock.service;

import dev.practice.stock.domain.Stock;
import dev.practice.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public void decreaseStock(Long id, Long quantity) {
        /**
         * 1. 재고 조회
         * 2. 재고 업데이트
         * 3. 재고 저장
         */
        Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

//        stockRepository.saveAndFlush(stock);
    }
}
