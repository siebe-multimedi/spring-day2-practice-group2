package com.example.practice.confirmation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

/**
 * A fake mail sender: it records the mails that leave the application. A mail that has been sent
 * cannot be taken back, just like a real one.
 */
@Component
public class FakeMailSender {

    private final List<String> sent = new CopyOnWriteArrayList<>();

    public void send(String reference) {
        sent.add(reference);
        System.out.println("[confirmation] confirmation mail sent for order " + reference);
    }

    public List<String> sent() {
        return List.copyOf(sent);
    }

    /** Used by the tests to start every test with an empty outbox. */
    public void clear() {
        sent.clear();
    }
}
