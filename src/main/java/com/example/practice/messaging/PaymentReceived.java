package com.example.practice.messaging;

/** The event that says: a payment was registered. */
public record PaymentReceived(Long paymentId) {
}
