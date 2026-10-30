package org.sid.accountservice.service;

import org.sid.accountservice.domain.Account;
import org.sid.accountservice.domain.AccountStatus;
import java.util.List;
import java.util.UUID;

public interface AccountService {
    List<Account> findAll();
    Account findById(UUID id);
    List<Account> findByCustomer(String customerReference);
    Account create(Account account);
    Account updateStatus(UUID id, AccountStatus status);
    Account debit(UUID id, double amount);
    Account credit(UUID id, double amount);
    void delete(UUID id);
}