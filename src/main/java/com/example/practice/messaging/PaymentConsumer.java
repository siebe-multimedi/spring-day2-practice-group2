package com.example.practice.messaging;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** The receiving side: books a payment in the ledger when its message arrives. */
@Component
public class PaymentConsumer {

    private final LedgerRepository ledger;
    private final ProcessedMessageRepository processed;

    public PaymentConsumer(LedgerRepository ledger, ProcessedMessageRepository processed) {
        this.ledger = ledger;
        this.processed = processed;
    }

    /** In a real application this is a message listener (with Spring AMQP: @RabbitListener). */
    @Transactional
    public void handle(String messageId, Long paymentId) {
        // TODO step 3: a broker delivers a message at least once, so duplicates will come.
        // Use the ProcessedMessage table (the id of a handled message is its primary key).
        ledger.save(new LedgerEntry(paymentId));
    }
}
