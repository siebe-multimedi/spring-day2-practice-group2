package com.example.practice.transactions;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    private final AccountRepository accounts;
    private final AttemptRepository attemptRepository;   // used by logAttempt() below
    private final AttemptLogger attempts;                // step 2: this is the bean you need

    public TransferService(AccountRepository accounts, AttemptRepository attemptRepository, AttemptLogger attempts) {
        this.accounts = accounts;
        this.attemptRepository = attemptRepository;
        this.attempts = attempts;
    }

    @Transactional   // TODO step 1: a failed transfer must be rolled back completely
    public void transfer(Long fromId, Long toId, long amount) throws InsufficientFundsException {
        logAttempt("transfer " + amount + " from account " + fromId + " to account " + toId);   // TODO step 2

        // Keep the order of these lines: the balances are changed first and checked afterwards, on purpose.
        // Fix step 1 with the transaction settings, not by moving the check.
        Account from = find(fromId);
        Account to = find(toId);
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
        if (from.getBalance() < 0) {
            throw new InsufficientFundsException("Not enough money on account " + fromId);
        }
    }

    // TODO step 2: the annotation is here, yet the attempt is lost when the transfer fails. Why?
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAttempt(String description) {
        attemptRepository.save(new AttemptEntry(description));
    }

    private Account find(Long id) {
        return accounts.findById(id).orElseThrow(() -> new IllegalArgumentException("No account " + id));
    }
}
