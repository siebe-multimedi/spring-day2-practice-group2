package com.example.practice.confirmation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SentMailRepository extends JpaRepository<SentMail, Long> {
}
