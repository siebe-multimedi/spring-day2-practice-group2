package com.example.practice.transactions;

public record TransferRequest(Long fromId, Long toId, long amount) {
}
