package dev.practice.stock.facade;

import dev.practice.stock.repository.RedisLockRepository;
import dev.practice.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockFacadeWithLettuceLock {

    private final StockService stockService;
    private final RedisLockRepository redisLockRepository;

    /**
     * lock 은 redis lettuce client 를 사용하여 분산 락
     * 실제 데이터는 mysql 에서 처리
     */

    public void decreaseStock(Long id, Long quantity) {

        // 스핀 락 방식
        while(!redisLockRepository.getLock(id)) {
            System.out.println("lock 획득 실패, 0.1 초 대기");
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("lock 획득 성공");
        try {
            stockService.decreaseStock(id, quantity);
        }finally {
            System.out.println("lock 해제");
            redisLockRepository.releaseLock(id);
        }
    }
}
