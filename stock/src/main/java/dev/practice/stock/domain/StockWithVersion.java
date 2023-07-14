package dev.practice.stock.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class StockWithVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long quantity;

    @Version
    private Long version;

    @Builder
    public StockWithVersion(Long productId, Long quantity) {
        this.id = null;
        this.productId = productId;
        this.quantity = quantity;
        this.version = 0L;
    }

    /**
     * 이 메서드에 synchronized 키워드를 붙이면 어떻게 될까?
     * - decrease 메서드를 호출하는 메서드(decreaseStock)에 @Transactional 어노테이션이 적용되어있으므로...
     * - JPA 쓰기 지연으로.. decreaseStock 메서드가 종료 될 때 DB 에 반영된다.
     * - 하지만, synchronized 키워드는 decrease 메서드에 적용되어 있으므로..
     * - decrease 메서드 종료와 decreaseStock 메서드 종료 시점 사이의 찰나의 순간에 동시성 문제가 생겨버린다.
     */
    public void decrease(Long quantity) {

        if(this.quantity - quantity < 0)
            throw new IllegalArgumentException("재고가 부족합니다.");

        this.quantity -= quantity;
    }
}
