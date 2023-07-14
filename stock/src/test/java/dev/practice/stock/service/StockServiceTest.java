package dev.practice.stock.service;

import dev.practice.stock.domain.Stock;
import dev.practice.stock.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {

        Stock stock = Stock.builder()
                .productId(1L)
                .quantity(100L)
                .build();

        stockRepository.save(stock);
    }

    @Transactional
    @Test
    void decreaseStock() {

        stockService.decreaseStock(1L, 10L);

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertEquals(90L, stock.getQuantity());
    }
}