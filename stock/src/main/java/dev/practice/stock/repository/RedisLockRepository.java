package dev.practice.stock.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class RedisLockRepository {

    /**
     * 동시성 문제 해결을 위해서 Redis, lettuce client 를 사용
     *
     * redis 의 setnx 명령어를 활용하여 분산락 구현
     * - setnx 는 set if not exist 줄임말이다.
     * - 방식은 어떤 키값를 정해놓고 redis 에서 데이터 생성 성공하면 lock 을 획득한 것이고,
     * - 이미 해당 키값으로 데이터 생성이 되어있으면 실패하는 방식이다.
     * - 해당 예제에서는 key = lock:{stock id}, value = lock 이다.
     * - redis 의 setnx 는 단순히 redis 에 어떤 키를 기준으로 데이터를 생성하고, 해당 키로 데이터가 이미 있으면 실패하는 것이므로..
     * - 해당 개념을 락으로 사용하기 위해서는 재시도 로직을 애플리케이션에서 구현해줘야한다. (해당 예제에서는 스핀락 개념을 사용)
     *
     * 참고
     * - spin lock 은 락 획득을 하고싶은 스레드가 반복적으로 락을 획득할 수 있는지 확인을 하는 방식이다.
     * - redis 에 지속적인 락 획득 가능 여부를 확인하기 때문에 redis 에 부하를 줄 수 있다.
     */

    private final StringRedisTemplate stringRedisTemplate;

    // 3초간 유효한 lock 을 생성한다.
    public Boolean getLock(Long key) {
        return stringRedisTemplate
                .opsForValue()
                .setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3000L));
    }

    public Boolean releaseLock(Long key) {
        return stringRedisTemplate.delete(generateKey(key));
    }

    private String generateKey(Long key) {
        return String.format("lock:%s", key);
    }
}
