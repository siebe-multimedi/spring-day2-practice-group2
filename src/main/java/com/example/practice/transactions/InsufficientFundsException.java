package com.example.practice.transactions;

/** A checked exception: it extends Exception, not RuntimeException. */
public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(String message) {
        super(message);
    }
}
