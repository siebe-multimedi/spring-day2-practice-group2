package com.example.practice.messaging;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A row of the outbox table: a message that still has to be sent (or was sent already). */
@Entity
@Table(name = "outbox_entries")
public class OutboxEntry {

    @Id
    @GeneratedValue
    private Long id;

    private String queue;

    private Long payload;

    private boolean sent;

    protected OutboxEntry() {
    }

    public OutboxEntry(String queue, Long payload) {
        this.queue = queue;
        this.payload = payload;
    }

    public Long getId() {
        return id;
    }

    public String getQueue() {
        return queue;
    }

    public Long getPayload() {
        return payload;
    }

    public boolean isSent() {
        return sent;
    }
}
