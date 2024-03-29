package dev.practice.stock.service;

import dev.practice.stock.domain.Stock;
import dev.practice.stock.repository.StockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void tearDown() {
        stockRepository.deleteAllInBatch();
    }

    // 기능 테스트
    @Test
    void decreaseStock() {

        // given
        Stock given = Stock.builder()
                .productId(99L)
                .quantity(100L)
                .build();
        stockRepository.save(given);

        // when
        stockServiceWithSynchronized.decreaseStock(given.getId(), 10L);

        // then
        Stock stock = stockRepository.findById(given.getId()).orElseThrow();
        assertEquals(90L, stock.getQuantity());
    }

    // 동시성 문제 해결
    @Test
    void concurrencyTestByThread100() {

        // given
        Stock given = Stock.builder()
                .productId(99L)
                .quantity(100L)
                .build();
        stockRepository.save(given);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    stockServiceWithSynchronized.decreaseStock(given.getId(), 1L);
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

        // then
        Stock stock = stockRepository.findById(given.getId()).orElseThrow();

        System.out.println("stock.getQuantity() = " + stock.getQuantity());

        Assertions.assertThat(stock.getQuantity()).isEqualTo(0L);
    }
}