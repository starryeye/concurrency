package dev.practice.stock.repository;

import dev.practice.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LockRepository extends JpaRepository<Stock, Long> {

    /**
     * 네임드 락을 이용한 동시성 문제 해결
     *
     * 네임드 락
     * - 이름을 가진 메타데이터 락이다.
     * - 비관적 락과 비슷하다. 락을 획득하고 헤제될 때까지 다른 세션은 락을 획득할 수 없다.
     * - - 비관적 락과의 차이점이라면 비관적 락은 접근데이터 그 자체에 락을 걸지만..
     * - - 네임드락은 별도의 공간에 락을 걸고 해제한다. redis 의 redisson 과 비슷
     * - 트랜잭션이 해제될때 락이 자동으로 해제 되지 않기 때문에 ... 해제를 직접해주거나 선점시간이 끝나면 해제가 된다.
     * - mysql 에서는 get_lock 명령어를 통해 네임드락을 획득할수 있다. release_lock 명령어를 통해 해제할수있다.
     * - 네임드락은 별도의 데이터소스로 진행해야한다. 실제 데이터의 데이터 소스에 네임드락을 사용하면 커넥션풀 부족할 수 있다.
     * - - 현재 예제에서는 개발 편의를 위해서 분리하지 않았다.
     *
     * 네임드 락은 분산락 (redis redisson 의 분산락) 과 거의 동일한 기능이다.
     * 그래서, 네임드 락을 사용할 바에는 redisson 을 사용할 것 같다.
     */

    // 3초간 락을 선점한다.
    @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(String key);
}
