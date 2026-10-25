package com.example.practice.consistency;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PriceService {

    private final ArticleRepository articles;

    public PriceService(ArticleRepository articles) {
        this.articles = articles;
    }

    /** Loads the article, doubles all prices with one bulk update, and loads the article again. */
    @Transactional
    public long doublePricesAndReload(Long id) {
        articles.findById(id).orElseThrow();     // the article is now in the persistence context
        articles.doublePrices();                  // a bulk update goes straight to the database
        return articles.findById(id).orElseThrow().getPrice();
    }
}
