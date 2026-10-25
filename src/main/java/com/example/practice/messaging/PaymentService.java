package com.example.practice.messaging;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository payments;
    private final MessageBroker broker;
    private final ApplicationEventPublisher events;   // step 1
    private final OutboxRepository outbox;            // step 2

    public PaymentService(PaymentRepository payments, MessageBroker broker,
                          ApplicationEventPublisher events, OutboxRepository outbox) {
        this.payments = payments;
        this.broker = broker;
        this.events = events;
        this.outbox = outbox;
    }

    @Transactional
    public void register(Payment payment) {
        payments.save(payment);
        broker.send("payments", payment.getId());   // TODO step 1 and 2: sent too early, and it can get lost
        if (payment.getAmount() <= 0) {
            throw new IllegalArgumentException("A payment must be positive");
        }
    }
}
