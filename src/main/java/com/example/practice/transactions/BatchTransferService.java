package com.example.practice.transactions;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchTransferService {

    private final TransferService transfers;

    public BatchTransferService(TransferService transfers) {
        this.transfers = transfers;
    }

    /** Runs all transfers. A transfer that fails must not stop or undo the others. Returns how many failed. */
    @Transactional   // TODO step 3: "one unit of work", but is that what we want here?
    public int transferAll(List<TransferRequest> requests) {
        int failed = 0;
        for (TransferRequest request : requests) {
            try {
                transfers.transfer(request.fromId(), request.toId(), request.amount());
            } catch (InsufficientFundsException e) {
                failed++;
            }
        }
        return failed;
    }
}
