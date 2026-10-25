package com.example.practice.confirmation;

import org.springframework.stereotype.Service;

@Service
public class OrderRegistrationService {

    private final PurchaseOrderRepository orders;
    // Add whatever else you need. You may create new classes.

    public OrderRegistrationService(PurchaseOrderRepository orders) {
        this.orders = orders;
    }

    /**
     * Registers an order. The requirements:
     *
     * 1. A valid order is stored as a PurchaseOrder. After the transaction has committed, a confirmation
     *    mail is sent (FakeMailSender.send) and a SentMail row is stored for the audit.
     * 2. An amount above 1000 is not allowed: throw a LimitExceededException. Nothing of that order may remain:
     *    the order is not stored, no mail is sent and no SentMail row is stored.
     * 3. A confirmation mail can not be taken back, so it must never be sent for an order that did not commit.
     *    Registering the same reference twice breaks the unique constraint of the reference when the
     *    transaction commits: no second mail may go out.
     * 4. register() may be called inside a larger transaction of the caller. If that transaction rolls back,
     *    nothing of the order may remain and no mail may go out.
     */
    public void register(String reference, long amount) throws LimitExceededException {
        // your code
    }
}
