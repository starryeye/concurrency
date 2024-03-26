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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

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
                .quantity(50L)
                .build();
        stockRepository.save(given);

        // when
        stockService.decreaseStock(given.getId(), 10L);

        // then
        Stock result = stockRepository.findById(given.getId()).orElseThrow();

        assertThat(result.getProductId()).isEqualTo(given.getProductId());
        assertThat(result.getQuantity()).isEqualTo(given.getQuantity() - 10L);
    }

    // 동시성 문제 발생
    @Test
    void concurrencyTest() throws InterruptedException {

        // given
        Stock given = Stock.builder()
                .productId(99L)
                .quantity(100L)
                .build();
        stockRepository.save(given);

        // when
        Thread thread1 = new Thread(() -> {
            stockService.decreaseStock(given.getId(), 10L);
        });

        Thread thread2 = new Thread(() -> {
            stockService.decreaseStock(given.getId(), 10L);
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        // then
        Stock result = stockRepository.findById(given.getId()).orElseThrow();

        System.out.println("stock.getQuantity() = " + result.getQuantity());

        Assertions.assertThatThrownBy(() -> {
            assertEquals(80L, result.getQuantity());
        }).isInstanceOf(AssertionError.class);

    }

    // 동시성 문제 발생
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
                    stockService.decreaseStock(given.getId(), 1L);
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
        Stock result = stockRepository.findById(given.getId()).orElseThrow();

        System.out.println("stock.getQuantity() = " + result.getQuantity());

        Assertions.assertThatThrownBy(() -> {
            assertThat(result.getQuantity()).isEqualTo(0L);
        }).isInstanceOf(AssertionError.class);
    }
}