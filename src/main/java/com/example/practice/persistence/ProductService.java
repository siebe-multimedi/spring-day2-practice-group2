package com.example.practice.persistence;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    // TODO step 1: first and second must be the SAME object. What is missing on this method?
    public boolean loadsTwiceGiveTheSameObject(Long id) {
        Product first = repository.findById(id).orElseThrow();
        Product second = repository.findById(id).orElseThrow();
        return first == second;
    }

    @Transactional
    public void applyDiscount(Long id, long percent) {
        // TODO step 2: lower the price by the given percentage, WITHOUT calling save()
    }

    @Transactional(readOnly = true)   // TODO step 3: the prices are not raised, and there is no error. Why?
    public void raiseAllPrices(long percent) {
        for (Product product : repository.findAll()) {
            product.setPrice(product.getPrice() * (100 + percent) / 100);
        }
    }

    public Product load(Long id) {
        return repository.findById(id).orElseThrow();
    }
}
