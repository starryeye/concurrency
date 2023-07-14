package dev.practice.stock.service;

import dev.practice.stock.domain.Stock;
import dev.practice.stock.repository.StockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StockServiceWithSynchronizedTest {

    @Autowired
    private StockServiceWithSynchronized stockServiceWithSynchronized;

    @Autowired
    private StockRepository stockRepository;

    private Long testId = 1L;

    @BeforeEach
    void setUp() {

        Stock stock = Stock.builder()
                .productId(1L)
                .quantity(100L)
                .build();

        stockRepository.save(stock);

        testId = stock.getId();
    }

    // 기능 테스트
    @Transactional
    @Test
    void decreaseStock() {

        stockServiceWithSynchronized.decreaseStock(testId, 10L);

        Stock stock = stockRepository.findById(testId).orElseThrow();

        assertEquals(90L, stock.getQuantity());
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
                    stockServiceWithSynchronized.decreaseStock(testId, 1L);
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

        Stock stock = stockRepository.findById(testId).orElseThrow();

        System.out.println("stock.getQuantity() = " + stock.getQuantity());

        Assertions.assertThat(stock.getQuantity()).isEqualTo(0L);
    }
}