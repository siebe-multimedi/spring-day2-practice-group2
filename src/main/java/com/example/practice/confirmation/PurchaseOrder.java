package com.example.practice.confirmation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String reference;

    private long amount;

    protected PurchaseOrder() {
    }

    public PurchaseOrder(String reference, long amount) {
        this.reference = reference;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public long getAmount() {
        return amount;
    }
}
