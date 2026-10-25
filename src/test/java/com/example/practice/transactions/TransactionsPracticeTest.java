package com.example.practice.transactions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Exercise 2: transactions. There is NO @Transactional on this class. */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)   // the steps run in order: step1, step2, step3
class TransactionsPracticeTest {

    @Autowired
    TransferService transfers;

    @Autowired
    BatchTransferService batch;

    @Autowired
    AccountRepository accounts;

    @Autowired
    AttemptRepository attempts;

    Long annaId;
    Long benId;

    @BeforeEach
    void clean() {
        accounts.deleteAllInBatch();
        attempts.deleteAllInBatch();
        annaId = accounts.save(new Account("Anna", 100)).getId();
        benId = accounts.save(new Account("Ben", 0)).getId();
        System.out.println("[transactions] ---- setup done: the test starts here ----");
    }

    @AfterEach
    void report() {
        System.out.println("[transactions] after the test: Anna = " + balance(annaId) + ", Ben = " + balance(benId)
                + ", attempts = " + attempts.count());
    }

    private long balance(Long id) {
        return accounts.findById(id).orElseThrow().getBalance();
    }

    @Test
    void step1_checkedException_theTransferMustRollBack() {
        assertThrows(InsufficientFundsException.class, () -> transfers.transfer(annaId, benId, 150));

        assertThat(balance(annaId)).as("Anna must still have 100").isEqualTo(100L);
        assertThat(balance(benId)).as("Ben must still have 0").isEqualTo(0L);
    }

    @Test
    void step2_requiresNew_theAttemptSurvivesTheRollback() {
        // The second account does not exist: this fails with a RuntimeException, so it always rolls back.
        assertThrows(IllegalArgumentException.class, () -> transfers.transfer(annaId, 999_999L, 10));

        assertThat(attempts.count()).as("the attempt must be logged, although the transfer was rolled back").isEqualTo(1L);
    }

    @Test
    void step3_batch_aFailedTransferDoesNotStopTheOthers() {
        int failed = batch.transferAll(List.of(
                new TransferRequest(annaId, benId, 30),
                new TransferRequest(annaId, benId, 500),     // fails: Anna does not have 500
                new TransferRequest(annaId, benId, 20)));

        assertThat(failed).as("one transfer failed").isEqualTo(1L);
        assertThat(balance(annaId)).as("Anna: 100 - 30 - 20").isEqualTo(50L);
        assertThat(balance(benId)).as("Ben: 30 + 20").isEqualTo(50L);
    }
}
