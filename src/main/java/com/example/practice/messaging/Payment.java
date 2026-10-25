package com.example.practice.messaging;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    private long amount;

    protected Payment() {
    }

    public Payment(long amount) {
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public long getAmount() {
        return amount;
    }
}
