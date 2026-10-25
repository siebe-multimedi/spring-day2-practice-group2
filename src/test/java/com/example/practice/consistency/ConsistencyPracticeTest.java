package com.example.practice.consistency;

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

/** Exercise 3: data consistency. There is NO @Transactional on this class. */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)   // the steps run in order: step1, step2, step3
class ConsistencyPracticeTest {

    @Autowired
    ArticleRepository articles;

    @Autowired
    StockItemRepository items;

    @Autowired
    StockService stock;

    @Autowired
    PriceService prices;

    @Autowired
    PlatformTransactionManager transactionManager;

    @BeforeEach
    void clean() {
        articles.deleteAllInBatch();
        items.deleteAllInBatch();
        stock.doWhileBusy(null);   // a leftover from an earlier test must not run now
        System.out.println("[consistency] ---- setup done: the test starts here ----");
    }

    @AfterEach
    void report() {
        System.out.println("[consistency] after the test: articles = " + articles.count() + ", stock items = " + items.count());
    }

    @Test
    void step1_optimisticLocking_theSecondSaveFails() {
        Long id = articles.save(new Article("Pen", 10)).getId();

        // Every findById() runs in its own transaction: Anna and Ben each get a detached copy.
        Article anna = articles.findById(id).orElseThrow();
        Article ben = articles.findById(id).orElseThrow();

        anna.setName("Fountain pen");
        articles.save(anna);                       // the first save wins

        ben.setPrice(15);                          // Ben still has the old name in his copy
        assertThrows(OptimisticLockingFailureException.class, () -> articles.save(ben));

        Article stored = articles.findById(id).orElseThrow();
        assertThat(stored.getName()).as("Anna's change must survive").isEqualTo("Fountain pen");
        assertThat(stored.getPrice()).as("Ben's change must be rejected, not merged").isEqualTo(10L);
    }

    @Test
    void step2_lastItem_theSecondOrderMustFail() {
        Long id = items.save(new StockItem("Concert ticket", 1)).getId();

        // While the first order is busy, a second order reserves the last ticket in its own transaction.
        TransactionTemplate separate = new TransactionTemplate(transactionManager);
        separate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        stock.doWhileBusy(() -> separate.executeWithoutResult(status -> stock.reserve(id, 1)));

        assertThrows(OutOfStockException.class, () -> stock.reserve(id, 1));

        assertThat(items.findById(id).orElseThrow().getQuantity()).as("one ticket was sold, not two").isEqualTo(0L);
    }

    @Test
    void step3_bulkUpdate_theNewPriceIsVisible() {
        Long id = articles.save(new Article("Pen", 10)).getId();

        long priceAfterBulkUpdate = prices.doublePricesAndReload(id);

        assertThat(priceAfterBulkUpdate).as("the price after the bulk update").isEqualTo(20L);
        assertThat(articles.findById(id).orElseThrow().getPrice()).as("the price in the database").isEqualTo(20L);
    }
}
