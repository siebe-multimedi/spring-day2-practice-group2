package com.example.practice.consistency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // TODO step 3: after this bulk update the persistence context still holds the OLD prices
    @Modifying
    @Query("update Article a set a.price = a.price * 2")
    int doublePrices();
}
