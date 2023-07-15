package dev.practice.stock.facade;

import dev.practice.stock.repository.LockRepository;
import dev.practice.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockFacadeWithNamedLock {

    private final StockService stockService;
    private final LockRepository lockRepository;

    @Transactional
    public void decreaseStock(Long id, Long quantity) {

        try {
            lockRepository.getLock(String.valueOf(id)); //실패나면 decreaseStock 메서드가 실행되지 않는다.
            stockService.decreaseStock(id, quantity); //트랜잭션 분리
        }finally {
            lockRepository.releaseLock(String.valueOf(id)); //실패나면 decreaseStock 과 상관 없고, 재시도 로직이 필요할 수 있을 듯..?
        }
    }
}
