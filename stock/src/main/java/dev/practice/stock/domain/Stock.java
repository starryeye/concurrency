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

    public void decrease(Long quantity) {

        if(this.quantity - quantity < 0)
            throw new IllegalArgumentException("재고가 부족합니다.");

        this.quantity -= quantity;
    }
}
