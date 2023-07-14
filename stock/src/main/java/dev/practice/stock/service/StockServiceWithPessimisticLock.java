package dev.practice.stock.service;

import dev.practice.stock.domain.Stock;
import dev.practice.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockServiceWithPessimisticLock {

    private final StockRepository stockRepository;

    /**
     * 동시성 문제 해결을 위해 비관적 락을 사용하였다.
     *
     * 특징
     * 1. 조회 부터 업데이트 까지 다른 트랜잭션은 해당 row 에 대해 락을 획득할 수 없다.
     * 2. DB 에서 락을 획득하기 때문에 분산 애플리케이션 환경에서도 동시성 문제가 없다.
     * 3. 락은 Table 단위 혹은 Row 단위로 걸린다.
     * - 해당 예제에서는 MySQL 의 InnoDB Storage Engine 을 사용하고 있으므로 인덱스에 락이 걸린다.
     * - PK 인 id 로 조회를 하고 있으므로 Row 단위로 락이 걸린다.
     * 4. 트래픽량이 많다면 낙관적 락보다 비관적 락이 더 좋은 성능을 보여준다.
     * 5. 예제에서는 단일 row 에 대해 락을 걸고 있지만, 여러 row(or table)에 걸쳐서 여러 락을 동시에 획득하면 데드락이 발생할 수 있다.
     * 6. 조회 부터 업데이트까지 길게 베타락으로 점유하기 때문에 트래픽량이 많다면 퍼포먼스가 좋지는 않다.
     */
    @Transactional
    public void decreaseStock(Long id, Long quantity) {
        /**
         * 1. 재고 조회
         * 2. 재고 업데이트
         * 3. 재고 저장
         */
        Stock stock = stockRepository.findByIdWithPessimisticLock(id).orElseThrow();

        stock.decrease(quantity);
    }
}
