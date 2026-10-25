package com.example.practice.transactions;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Writes a row in the attempts table. */
@Component
public class AttemptLogger {

    private final AttemptRepository attempts;

    public AttemptLogger(AttemptRepository attempts) {
        this.attempts = attempts;
    }

    // TODO step 2: this method must run in its own transaction
    @Transactional
    public void log(String description) {
        attempts.save(new AttemptEntry(description));
    }
}
