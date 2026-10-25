package com.example.practice.confirmation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Exercise 6: an assignment with four requirements, not steps. See the Javadoc of
 * OrderRegistrationService.register(). There is NO @Transactional on this class.
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class ConfirmationPracticeTest {

    @Autowired
    OrderRegistrationService service;

    @Autowired
    PurchaseOrderRepository orders;

    @Autowired
    SentMailRepository sentMails;

    @Autowired
    FakeMailSender mailSender;

    @Autowired
    PlatformTransactionManager transactionManager;

    @BeforeEach
    void clean() {
        orders.deleteAllInBatch();
        sentMails.deleteAllInBatch();
        mailSender.clear();
        System.out.println("[confirmation] ---- setup done: the test starts here ----");
    }

    @AfterEach
    void report() {
        System.out.println("[confirmation] after the test: orders = " + orders.count() + ", mails sent = "
                + mailSender.sent().size() + ", SentMail rows = " + sentMails.count());
    }

    @Test
    void requirement1_validOrder_isStoredAndConfirmed() throws Exception {
        service.register("ORD-1", 100);

        assertThat(orders.count()).as("the order is stored").isEqualTo(1L);
        assertThat(mailSender.sent()).as("one confirmation mail").hasSize(1);
        assertThat(sentMails.count()).as("the sent mail is recorded in the audit table").isEqualTo(1L);
    }

    @Test
    void requirement2_overTheLimit_leavesNothingBehind() {
        assertThrows(LimitExceededException.class, () -> service.register("ORD-2", 5000));

        assertThat(orders.count()).as("the order must not be stored").isZero();
        assertThat(mailSender.sent()).as("no mail").isEmpty();
        assertThat(sentMails.count()).as("no audit row").isZero();
    }

    @Test
    void requirement3_failingCommit_sendsNoMail() throws Exception {
        service.register("ORD-3", 100);

        // The same reference again: the unique constraint breaks when the transaction commits.
        assertThrows(Exception.class, () -> service.register("ORD-3", 200));

        assertThat(orders.count()).as("only the first order is stored").isEqualTo(1L);
        assertThat(mailSender.sent()).as("only the first order is confirmed").hasSize(1);
        assertThat(sentMails.count()).as("only one audit row").isEqualTo(1L);
    }

    @Test
    void requirement4_callerRollsBack_sendsNoMail() {
        // register() is called inside a larger transaction of the caller, and that transaction rolls back.
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            try {
                service.register("ORD-4", 100);
            } catch (LimitExceededException e) {
                throw new IllegalStateException(e);
            }
            status.setRollbackOnly();
        });

        assertThat(orders.count()).as("the order was rolled back").isZero();
        assertThat(mailSender.sent()).as("no mail for an order that was rolled back").isEmpty();
        assertThat(sentMails.count()).as("no audit row").isZero();
    }
}
