package com.example.practice.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Exercise 1: the persistence context. There is NO @Transactional on this class. */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)   // the steps run in order: step1, step2, step3
class PersistencePracticeTest {

    @Autowired
    ProductService service;

    @Autowired
    ProductRepository products;

    Long laptopId;
    Long phoneId;

    @BeforeEach
    void clean() {
        products.deleteAllInBatch();
        laptopId = products.save(new Product("Laptop", 1000)).getId();
        phoneId = products.save(new Product("Phone", 500)).getId();
        System.out.println("[persistence] ---- setup done: the test starts here ----");
    }

    @AfterEach
    void report() {
        String prices = products.findAll().stream()
                .map(p -> p.getName() + " = " + p.getPrice())
                .collect(Collectors.joining(", "));
        System.out.println("[persistence] after the test: " + prices);
    }

    @Test
    void step1_oneTransaction_oneInstance() {
        boolean same = service.loadsTwiceGiveTheSameObject(laptopId);

        assertThat(same).as("both findById() calls must return the same object").isTrue();
    }

    @Test
    void step2_dirtyChecking_noSaveNeeded() {
        service.applyDiscount(laptopId, 10);

        assertThat(service.load(laptopId).getPrice()).as("laptop after a 10% discount").isEqualTo(900L);
    }

    @Test
    void step3_readOnly_pricesAreRaised() {
        service.raiseAllPrices(10);

        assertThat(service.load(laptopId).getPrice()).as("laptop after +10%").isEqualTo(1100L);
        assertThat(service.load(phoneId).getPrice()).as("phone after +10%").isEqualTo(550L);
    }
}
