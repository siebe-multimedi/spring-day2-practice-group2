package com.example.practice.messaging;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** One row for every payment that was booked in the ledger. */
@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    @GeneratedValue
    private Long id;

    private Long paymentId;

    protected LedgerEntry() {
    }

    public LedgerEntry(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getId() {
        return id;
    }

    public Long getPaymentId() {
        return paymentId;
    }
}
