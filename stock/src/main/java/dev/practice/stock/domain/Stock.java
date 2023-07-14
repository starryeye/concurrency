package dev.practice.stock.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long quantity;

    @Builder
    public Stock(Long id, Long productId, Long quantity) {
        this.id = null;
        this.productId = productId;
        this.quantity = quantity;
    }

    /**
     * 이 메서드에 synchronized 키워드를 붙이면 어떻게 될까?
     * - decrease 메서드를 호출하는 메서드(decreaseStock)에 @Transactional 어노테이션이 적용되어있으므로...
     * - JPA 쓰기 지연으로.. decreaseStock 메서드가 종료 될 때 DB 에 반영된다.
     * - 하지만, synchronized 키워드는 decrease 메서드에 적용되어 있으므로..
     * - decrease 메서드 종료와 decreaseStock 메서드 종료 시점 사이의 찰나의 순간에 동시성 문제가 생겨버린다.
     *
     * 참고
     * - synchronized 키워드를 붙이고 동시성 문제를 해결하고 싶다면 decreaseStock 메서드에 synchronized 키워드를 붙이고
     * - @Transactional 어노테이션을 제거 해야한다.
     * (JPA 쓰기 지연은 메서드 종료후 수행되므로 제거 하지 않으면 메서드 종료 시점과 쓰기지연 시점 사이에 동시성 문제가 발생한다.)
     * - @Transactional 어노테이션이 제거 되었기 때문에 쓰기 지연이 동작하지 못하므로 stockRepository.saveAndFlush(stock); 를 추가해야한다.
     * -> StockServiceWithSynchronized 에 구현
     */
    public synchronized void decrease(Long quantity) {

        if(this.quantity - quantity < 0)
            throw new IllegalArgumentException("재고가 부족합니다.");

        this.quantity -= quantity;
    }
}
