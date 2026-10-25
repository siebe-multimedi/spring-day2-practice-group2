package com.example.practice.transactions;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** One row per transfer attempt, successful or not. */
@Entity
@Table(name = "attempts")
public class AttemptEntry {

    @Id
    @GeneratedValue
    private Long id;

    private String description;

    protected AttemptEntry() {
    }

    public AttemptEntry(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
