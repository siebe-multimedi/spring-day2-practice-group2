package com.example.practice.consistency;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockItemRepository items;
    private Runnable whileBusy;

    public StockService(StockItemRepository items) {
        this.items = items;
    }

    @Transactional
    public void reserve(Long id, int amount) {
        StockItem item = items.findById(id).orElseThrow();
        if (item.getQuantity() < amount) {
            throw new OutOfStockException(id);
        }
        pretendToBeBusy();   // keep this line, BEFORE your update: the test simulates a second order here
        item.setQuantity(item.getQuantity() - amount);          // TODO step 2: check-then-act is not safe
    }

    /** Used by the test: simulates another order that sneaks in while this one is busy. Runs only once. */
    public void doWhileBusy(Runnable work) {
        this.whileBusy = work;
    }

    private void pretendToBeBusy() {
        Runnable work = whileBusy;
        whileBusy = null;
        if (work != null) {
            work.run();
        }
    }
}
