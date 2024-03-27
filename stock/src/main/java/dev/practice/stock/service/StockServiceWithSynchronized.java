package dev.practice.stock.service;

import dev.practice.stock.domain.Stock;
import dev.practice.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockServiceWithSynchronized {

    private final StockRepository stockRepository;

    /**
     * 동시성 문제 해결하기 위해 StockService 와 비교하여 달라진 점.
     *
     * - 동시성 문제 해결을 위해 synchronized 키워드를 붙였다.
     *
     * - @Transactional 어노테이션을 제거했다.
     *  JPA 쓰기 지연은 메서드 종료후 수행되므로 제거 하지 않으면
     *  메서드 종료 시점과 쓰기지연 시점 사이에 빈틈이 생겨 동시성 문제가 생길 수 있다.
     *
     * - @Transactional 어노테이션이 제거 되었기 때문에 쓰기 지연이 동작하지 못하므로
     *  stockRepository.saveAndFlush(stock); 를 추가해야한다.
     *  synchronized 범위내에 commit 이 이루어지므로 동시성 문제가 발생하지 않는다.
     * 
     * 참고
     * - synchronized 키워드는 단일 JVM 에서만 동작하므로 분산 환경에서는 여전히 동시성 문제가 발생한다.
     */
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
