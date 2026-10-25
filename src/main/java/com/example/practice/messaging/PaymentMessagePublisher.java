package com.example.practice.messaging;

import org.springframework.stereotype.Component;

@Component
public class PaymentMessagePublisher {

    private final MessageBroker broker;

    public PaymentMessagePublisher(MessageBroker broker) {
        this.broker = broker;
    }

    // TODO step 1: this method must only run AFTER the transaction has committed
    // (hint: @TransactionalEventListener with phase = TransactionPhase.AFTER_COMMIT)
    public void on(PaymentReceived event) {
        broker.send("payments", event.paymentId());
    }
}
