package com.example.practice.messaging;

import org.springframework.stereotype.Component;

/** Step 2: sends the messages that are waiting in the outbox. */
@Component
public class OutboxRelay {

    private final OutboxRepository outbox;
    private final MessageBroker broker;

    public OutboxRelay(OutboxRepository outbox, MessageBroker broker) {
        this.outbox = outbox;
        this.broker = broker;
    }

    /** In a real application this runs on a schedule (@Scheduled). Here the tests call it. */
    public void publishPending() {
        // TODO step 2: send every unsent entry (outbox.findUnsent()) and mark it as sent (outbox.markSent(id)).
        // If the broker is down, stop and leave the entry in the outbox.
    }
}
