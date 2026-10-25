package com.example.practice.booking;

import org.springframework.stereotype.Service;

/** Books a seat and tries again when another booking got in the way. Not transactional, on purpose. */
@Service
public class BookingService {

    private final BookingTransaction transaction;

    public BookingService(BookingTransaction transaction) {
        this.transaction = transaction;
    }

    public void bookWithRetry(Long flightId) {
        // TODO step 3: call transaction.bookSeat(flightId). When it fails because of a conflict
        // (an optimistic locking failure), try again, with at most 3 attempts in total.
        // Other exceptions must NOT be retried.
    }
}
