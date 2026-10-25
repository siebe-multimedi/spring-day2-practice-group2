package com.example.practice.messaging;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OutboxRepository extends JpaRepository<OutboxEntry, Long> {

    @Query("select e from OutboxEntry e where e.sent = false order by e.id")
    List<OutboxEntry> findUnsent();

    /** Marks one row as sent, in its own short transaction. */
    @Modifying
    @Transactional
    @Query("update OutboxEntry e set e.sent = true where e.id = :id")
    void markSent(@Param("id") Long id);
}
