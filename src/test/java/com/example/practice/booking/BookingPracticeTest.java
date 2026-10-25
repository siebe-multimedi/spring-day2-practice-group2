package com.example.practice.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/** Exercise 5: booking with a retry. There is NO @Transactional on this class. */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)   // the steps run in order: step1, step2, step3
class BookingPracticeTest {

    @Autowired
    BookingTransaction transaction;

    @Autowired
    BookingService booking;

    @Autowired
    FlightRepository flights;

    @Autowired
    PlatformTransactionManager transactionManager;

    @BeforeEach
    void clean() {
        flights.deleteAllInBatch();
        transaction.doWhileBusy(null);   // a leftover from an earlier test must not run now
        System.out.println("[booking] ---- setup done: the test starts here ----");
    }

    @AfterEach
    void report() {
        String seats = flights.findAll().stream()
                .map(f -> f.getCode() + " has " + f.getSeatsLeft() + " seats left")
                .collect(java.util.stream.Collectors.joining(", "));
        System.out.println("[booking] after the test: " + seats);
    }

    private Long flight(int seats) {
        return flights.save(new Flight("SN100", seats)).getId();
    }

    private long seatsLeft(Long id) {
        return flights.findById(id).orElseThrow().getSeatsLeft();
    }

    /** While the first booking is busy, a second customer books a seat in a transaction of their own. */
    private void secondCustomerBooksWhileTheFirstIsBusy(Long id) {
        TransactionTemplate separate = new TransactionTemplate(transactionManager);
        separate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transaction.doWhileBusy(() -> separate.executeWithoutResult(status -> transaction.bookSeat(id)));
    }

    @Test
    void step1_bookSeat_takesSeatsUntilSoldOut() {
        Long id = flight(2);

        transaction.bookSeat(id);
        assertThat(seatsLeft(id)).as("after the first booking").isEqualTo(1L);

        transaction.bookSeat(id);
        assertThat(seatsLeft(id)).as("after the second booking").isEqualTo(0L);

        assertThrows(SoldOutException.class, () -> transaction.bookSeat(id));
    }

    @Test
    void step2_conflict_theSlowerBookingFails() {
        Long id = flight(5);
        secondCustomerBooksWhileTheFirstIsBusy(id);

        assertThrows(OptimisticLockingFailureException.class, () -> transaction.bookSeat(id));

        assertThat(seatsLeft(id)).as("only the booking that committed first is stored").isEqualTo(4L);
    }

    @Test
    void step3_retry_bothCustomersGetASeat() {
        Long id = flight(5);
        secondCustomerBooksWhileTheFirstIsBusy(id);

        booking.bookWithRetry(id);   // must not throw: the first attempt fails, the second one succeeds

        assertThat(seatsLeft(id)).as("two seats were booked").isEqualTo(3L);
    }

    @Test
    void step3_retry_soldOutIsNotRetried() {
        Long id = flight(0);

        int before = transaction.calls();
        assertThrows(SoldOutException.class, () -> booking.bookWithRetry(id));

        assertThat(transaction.calls() - before).as("a sold out flight is tried once, not retried").isEqualTo(1L);
    }
}
