package dev.practice.stock.service;

import dev.practice.stock.domain.Stock;
import dev.practice.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockServiceWithSynchronized {

    private final StockRepository stockRepository;

    /**
     * 동시성 문제 해결하기 위해 StockService 와 비교하여 달라진 점.
     *
     * - 동시성 문제 해결을 위해 synchronized 키워드를 붙였다.
     *
     * - @Transactional 어노테이션을 제거했다.
     *  JPA 쓰기 지연은 메서드 종료후 수행되므로 제거 하지 않으면
     *  메서드 종료 시점과 쓰기지연 시점 사이에 빈틈이 생겨 동시성 문제가 생길 수 있다.
     *
     * - @Transactional 어노테이션이 제거 되었기 때문에 쓰기 지연이 동작하지 못하므로
     *  stockRepository.saveAndFlush(stock); 를 추가해야한다.
     *  synchronized 범위내에 commit 이 이루어지므로 동시성 문제가 발생하지 않는다.
     * 
     * 참고
     * - synchronized 키워드는 단일 JVM 에서만 동작하므로 분산 환경에서는 여전히 동시성 문제가 발생한다.
     */
    public synchronized void decreaseStock(Long id, Long quantity) {
        /**
         * 1. 재고 조회
         * 2. 재고 업데이트
         * 3. 재고 저장
         */
        Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }
    /**
     * synchronized 사용 2 가지
     *
     * 메서드에 synchronized
     * 해당 메서드가 속한 객체의 인스턴스를 락으로 사용한다.
     * 같은 인스턴스의 다른 synchronized 메서드는 동일한 락을 사용하기 때문에,
     * 한 스레드 A 가 어떤 synchronized 메서드를 실행하고 있고,
     * 다른 스레드 B 가 동일한 인스턴스의 다른 synchronized 메서드를 호출하면..
     * A 스레드가 메소드 실행을 완료하고 락을 해제할 때까지 대기해야 한다.
     *
     * 블럭에 synchronized
     * synchronized 블록을 사용하면 더 세밀한 동기화 제어를 할 수 있다.
     * 지정된 객체를 락으로 사용하는 것이다.
     * - synchronized(this) { /락 내부/ }
     * 이렇게 하면, 해당 인스턴스를 락으로 사용하는 것이다. (메서드에 synchronized 된 것과 동일한 묶음이 된다.)
     *
     * 참고
     * static 메서드에 synchronized 를 하면, 해당 클래스의 "Class" 인스턴스가 락으로 사용되는 것이다.
     * static 메서드 끼리 락이 걸리는 것이다.
     *
     * 참고
     * synchronized 블럭에서 락을 this 로 하지 않고 다른 인스턴스로 잡아서 별도의 락 객체를 만들어 줄 수 도 있다.
     *
     * 참고
     * 변수에 대해 synchronized 하고 싶으면 wrapping 객체를 만들고 해당 변수에 접근하는 모든 메서드를 synchronized 해보자..
     */
}
