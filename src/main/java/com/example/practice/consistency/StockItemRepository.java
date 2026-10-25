package com.example.practice.consistency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {

    // TODO step 2: add a method that checks and changes the quantity in ONE statement and returns
    // the number of rows it changed (@Modifying + @Query, see the slide)
}
