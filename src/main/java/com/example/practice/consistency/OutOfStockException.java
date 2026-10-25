package com.example.practice.consistency;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(Long itemId) {
        super("Item " + itemId + " is out of stock");
    }
}
