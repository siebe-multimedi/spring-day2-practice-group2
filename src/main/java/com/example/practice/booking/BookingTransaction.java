package com.example.practice.booking;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** One booking, in one transaction. */
@Service
public class BookingTransaction {

    private final FlightRepository flights;
    private final AtomicInteger calls = new AtomicInteger();
    private Runnable whileBusy;

    public BookingTransaction(FlightRepository flights) {
        this.flights = flights;
    }

    @Transactional
    public void bookSeat(Long flightId) {
        calls.incrementAndGet();   // keep this line: the test counts the attempts
        // TODO step 1: load the flight
        // TODO step 1: throw a SoldOutException (with the flight code) when no seats are left
        pretendToBeBusy();   // keep this line: the test simulates a second customer here
        // TODO step 1: take one seat
    }

    /** Used by the test: how many times bookSeat() was called. */
    public int calls() {
        return calls.get();
    }

    /** Used by the test: simulates another customer who books while this booking is busy. Runs only once. */
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
