package com.example.practice.confirmation;

/** A checked exception: it extends Exception, not RuntimeException. */
public class LimitExceededException extends Exception {

    public LimitExceededException(String message) {
        super(message);
    }
}
