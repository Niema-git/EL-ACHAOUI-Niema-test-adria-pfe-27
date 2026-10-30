package org.sid.accountservice.web.rest;

import lombok.RequiredArgsConstructor;
import org.sid.accountservice.domain.Account;
import org.sid.accountservice.domain.AccountStatus;
import org.sid.accountservice.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountService accountService;

    @GetMapping
    public List<Account> getAll(@RequestParam(required = false) String customerReference) {
        return customerReference == null
                ? accountService.findAll()
                : accountService.findByCustomer(customerReference);
    }

    @GetMapping("/{id}")
    public Account getById(@PathVariable UUID id) {
        return accountService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account create(@RequestBody Account account) {
        return accountService.create(account);
    }

    @PatchMapping("/{id}/status")
    public Account updateStatus(@PathVariable UUID id, @RequestParam AccountStatus status) {
        return accountService.updateStatus(id, status);
    }

    @PostMapping("/{id}/debit")
    public Account debit(@PathVariable UUID id, @RequestParam double amount) {
        return accountService.debit(id, amount);
    }

    @PostMapping("/{id}/credit")
    public Account credit(@PathVariable UUID id, @RequestParam double amount) {
        return accountService.credit(id, amount);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        accountService.delete(id);
    }
}