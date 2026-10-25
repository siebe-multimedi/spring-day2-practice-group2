package com.example.practice.confirmation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** The audit row that says: a confirmation mail was sent for this order reference. */
@Entity
@Table(name = "sent_mails")
public class SentMail {

    @Id
    @GeneratedValue
    private Long id;

    private String reference;

    protected SentMail() {
    }

    public SentMail(String reference) {
        this.reference = reference;
    }

    public Long getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }
}
