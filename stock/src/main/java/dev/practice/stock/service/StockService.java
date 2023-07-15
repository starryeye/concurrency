package dev.practice.stock.service;

import dev.practice.stock.domain.Stock;
import dev.practice.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    /**
     * requires_new 를 사용한 이유
     * - StockFacadeWithNamedLock 에서 상위에 @Transactional 과 분리하기 위해서이다.
     * - named lock 은 원래 실제 데이터와 별도의 datasource 로 관리되어야한다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
