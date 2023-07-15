package dev.practice.stock.facade;

import dev.practice.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class StockFacadeWithRedissonLock {

    /**
     * 동시성 문제 해결을 위해 Redis, Redisson client 를 사용
     * - Redis 의 pub/sub 기반으로 lock 구현 제공
     * - Redis 의 pub/sub 을 활용하여 어떤 채널을 지정해놓는다. (채널이 락 개념이다.)
     * - 락을 가진 스레드가 락을 반환하면 채널을 통해서 반환 사실을 알리게 되고,
     * - 락 획득 대기하는 스레드가 락 해제 메시지를 받으면 락을 획득할 수있는 방식이다.
     * - 락 해제 메시지가 오면 락 획득을 하는 방식이므로, lettuce client 의 분산락 구현에서의 스핀 락 보다 redis 부하가 적을 수 있다.
     *
     * 더 자세한 내용은 https://redis.io/topics/distlock 참고
     * repo Redis/DistributedLock 참고
     */

    private final RedissonClient redissonClient;
    private final StockService stockService;

    public void decreaseStock(Long id, Long quantity) {

        RLock lock = redissonClient.getLock(generateKey(id));

        try {
            // 락 획득 하지 못할 시 5초간 대기, 락 획득 시 1초 동안 락 점유
            boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);

            if(!available) {
                System.out.println("lock 획득 실패");
                return;
            }

            System.out.println("lock 획득 성공");
            stockService.decreaseStock(id, quantity);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("lock 해제");
            lock.unlock();
        }
    }

    private String generateKey(Long key) {
        return String.format("lock:%s", key);
    }
}
