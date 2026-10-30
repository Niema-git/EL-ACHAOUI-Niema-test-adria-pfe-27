package org.sid.transactionservice.service;

import org.sid.transactionservice.domain.Transaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    List<Transaction> findAll();
    Transaction findById(UUID id);
    List<Transaction> findByAccount(UUID accountId);
    Transaction create(Transaction transaction);
    List<Transaction> transfer(UUID sourceId, UUID targetId, BigDecimal amount, String currency);
}