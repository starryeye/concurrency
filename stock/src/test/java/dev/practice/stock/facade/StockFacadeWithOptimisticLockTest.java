package dev.practice.stock.facade;

import dev.practice.stock.domain.StockWithVersion;
import dev.practice.stock.repository.StockRepositoryWithOptimisticLock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockFacadeWithOptimisticLockTest {

    @Autowired
    private StockFacadeWithOptimisticLock stockFacadeWithOptimisticLock;

    @Autowired
    private StockRepositoryWithOptimisticLock stockRepositoryWithOptimisticLock;

    private Long testId = 1L;

    @BeforeEach
    void setUp() {

        StockWithVersion stockWithVersion = StockWithVersion.builder()
                .productId(1L)
                .quantity(100L)
                .build();

        stockRepositoryWithOptimisticLock.save(stockWithVersion);
        testId = stockWithVersion.getId();


        System.out.println("testId = " + testId);
    }

    // 기능 테스트
    @Transactional
    @Test
    void decreaseStock() throws InterruptedException {

        stockFacadeWithOptimisticLock.decreaseStock(testId, 10L);

        StockWithVersion stockWithVersion = stockRepositoryWithOptimisticLock.findById(testId).orElseThrow();

        assertEquals(90L, stockWithVersion.getQuantity());
    }

    // 동시성 문제 해결
    @Test
    void concurrencyTestByThread100() {

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    stockFacadeWithOptimisticLock.decreaseStock(testId, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        StockWithVersion stockWithVersion = stockRepositoryWithOptimisticLock.findById(testId).orElseThrow();

        System.out.println("stock.getQuantity() = " + stockWithVersion.getQuantity());
        System.out.println("stock.getVersion() = " + stockWithVersion.getVersion());

        Assertions.assertThat(stockWithVersion.getQuantity()).isEqualTo(0L);
    }

}