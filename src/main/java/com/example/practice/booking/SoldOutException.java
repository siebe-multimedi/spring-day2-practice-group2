package com.example.practice.booking;

public class SoldOutException extends RuntimeException {

    public SoldOutException(String flightCode) {
        super("Flight " + flightCode + " is sold out");
    }
}
