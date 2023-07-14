package dev.practice.stock.service;

import dev.practice.stock.domain.StockWithVersion;
import dev.practice.stock.repository.StockRepositoryWithOptimisticLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockServiceWithOptimisticLock {

    private final StockRepositoryWithOptimisticLock stockRepositoryWithOptimisticLock;

    /**
     * 동시성 문제 해결을 위해 낙관적 락을 사용하였다.
     *
     * 특징
     * 1. DB에서는 락을 걸지 않는다. 업데이트 하려는 데이터의 column 에 version 이 존재한다.
     * 따라서, 애플리케이션 레벨에서 논리적인 락을 건 셈이다.
     * 2. 조회 시점에 락이 걸리지 않으므로 비관적 락에 비해 락의 범위가 좁아서 성능이 좋다.
     * 업데이트 때는 락이 걸리는데 베타락으로 찰나의 순간일 뿐이다.
     * 3. 비관적 락과는 다르게 무조건 업데이트를 하려는 특정 row 에만 락이 걸린다.
     * 4. 트래픽이 많다면 결국 재시도를 여러번 해야하므로 성능이 좋지는 않다.
     * 5. 따라서, 도메인 특성상 조회가 많고 업데이트가 적다면 낙관적 락이 적합할 수 있다.
     * 6. 재시도 로직을 애플리케이션 레벨에서 구현해야 한다.
     * 7. Compare and Swap(CAS) 알고리즘 방식이다. (Atomic 클래스가 대표적)
     */
    @Transactional
    public void decreaseStock(Long id, Long quantity) {
        /**
         * 1. 재고 조회
         * 2. 재고 업데이트
         * 3. 재고 저장
         */
        StockWithVersion stockWithVersion = stockRepositoryWithOptimisticLock.findById(id).orElseThrow();

        stockWithVersion.decrease(quantity);
    }
}
