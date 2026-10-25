package com.example.practice.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Exercise 4: asynchronous integration. There is NO @Transactional on this class. */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)   // the steps run in order: step1, step2, step3
class MessagingPracticeTest {

    @Autowired
    PaymentService service;

    @Autowired
    OutboxRelay relay;

    @Autowired
    PaymentConsumer consumer;

    @Autowired
    FakeMessageBroker broker;

    @Autowired
    PaymentRepository payments;

    @Autowired
    OutboxRepository outbox;

    @Autowired
    LedgerRepository ledger;

    @Autowired
    ProcessedMessageRepository processed;

    @BeforeEach
    void clean() {
        payments.deleteAllInBatch();
        outbox.deleteAllInBatch();
        ledger.deleteAllInBatch();
        processed.deleteAllInBatch();
        broker.clear();
        System.out.println("[messaging] ---- setup done: the test starts here ----");
    }

    @AfterEach
    void report() {
        broker.setFailing(false);
        System.out.println("[messaging] after the test: payments = " + payments.count() + ", outbox entries = " + outbox.count()
                + ", messages sent = " + broker.sentMessages().size() + ", ledger entries = " + ledger.count());
    }

    @Test
    void step1_failedPayment_sendsNoMessage() {
        assertThrows(IllegalArgumentException.class, () -> service.register(new Payment(-5)));
        relay.publishPending();

        assertThat(payments.count()).as("the payment was rolled back").isZero();
        assertThat(broker.sentMessages()).as("no message for a payment that was rolled back").isEmpty();
    }

    @Test
    void step1_successfulPayment_sendsOneMessage() {
        service.register(new Payment(25));
        relay.publishPending();

        assertThat(broker.sentMessages()).as("one message for one payment").hasSize(1);
    }

    @Test
    void step2_brokerDown_theMessageIsNotLost() {
        broker.setFailing(true);

        service.register(new Payment(25));          // must not throw: the payment is saved
        relay.publishPending();                      // the broker is still down: nothing is sent, nothing is lost
        assertThat(payments.count()).as("the payment is saved").isEqualTo(1L);
        assertThat(broker.sentMessages()).as("nothing could be sent yet").isEmpty();

        broker.setFailing(false);
        relay.publishPending();
        assertThat(broker.sentMessages()).as("the message arrives when the broker is back").hasSize(1);

        relay.publishPending();                      // run it again: nothing new may be sent
        assertThat(broker.sentMessages()).as("sent once, not twice").hasSize(1);
    }

    @Test
    void step3_idempotentConsumer_theSameMessageTwice() {
        consumer.handle("msg-1", 42L);
        consumer.handle("msg-1", 42L);               // the broker delivers the same message a second time
        assertThat(ledger.count()).as("one ledger entry for message msg-1").isEqualTo(1L);

        consumer.handle("msg-2", 43L);               // a different message must still be handled
        assertThat(ledger.count()).as("a new message is handled").isEqualTo(2L);
    }
}
