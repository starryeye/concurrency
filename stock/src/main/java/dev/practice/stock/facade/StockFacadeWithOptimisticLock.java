package dev.practice.stock.facade;

import dev.practice.stock.service.StockServiceWithOptimisticLock;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockFacadeWithOptimisticLock {

    private final StockServiceWithOptimisticLock stockServiceWithOptimisticLock;

    public void decreaseStock(Long id, Long quantity) throws InterruptedException {

        // 재시도 로직
        while (true) {
            try {
                stockServiceWithOptimisticLock.decreaseStock(id, quantity);
                break;
            }catch (OptimisticLockingFailureException e) {
                System.out.println("OptimisticLockingFailureException 발생");
                Thread.sleep(50L);
            }
        }
    }
}
