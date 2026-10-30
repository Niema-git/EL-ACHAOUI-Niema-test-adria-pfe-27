package org.sid.transactionservice.web.rest;

import org.sid.transactionservice.domain.Transaction;
import org.sid.transactionservice.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

    private final TransactionService transactionService;

    public TransactionRestController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<Transaction> getAll(@RequestParam(required = false) UUID accountId) {
        return accountId == null
                ? transactionService.findAll()
                : transactionService.findByAccount(accountId);
    }

    @GetMapping("/{id}")
    public Transaction getById(@PathVariable UUID id) {
        return transactionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction create(@RequestBody Transaction transaction) {
        return transactionService.create(transaction);
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Transaction> transfer(@RequestParam UUID sourceId,
                                      @RequestParam UUID targetId,
                                      @RequestParam BigDecimal amount,
                                      @RequestParam String currency) {
        return transactionService.transfer(sourceId, targetId, amount, currency);
    }
}