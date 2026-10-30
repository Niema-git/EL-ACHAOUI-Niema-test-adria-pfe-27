package org.sid.accountservice.service;

import lombok.RequiredArgsConstructor;
import org.sid.accountservice.domain.Account;
import org.sid.accountservice.domain.AccountStatus;
import org.sid.accountservice.repository.AccountRepository;
import org.sid.accountservice.service.exception.AccountNotFoundException;
import org.sid.accountservice.service.exception.BusinessRuleException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Override
    public List<Account> findByCustomer(String customerReference) {
        return accountRepository.findByCustomerReference(customerReference);
    }

    @Override
    public Account create(Account account) {
        if (account.getBalance() < 0) {
            throw new BusinessRuleException("Le solde initial ne peut pas être négatif");
        }
        account.setId(null);                                  // l'id est généré par JPA
        account.setCurrency(account.getCurrency().toUpperCase());
        if (account.getStatus() == null) {
            account.setStatus(AccountStatus.ACTIVE);
        }
        return accountRepository.save(account);
    }

    @Override
    public Account updateStatus(UUID id, AccountStatus status) {
        Account account = findById(id);
        account.setStatus(status);
        return account;
    }

    @Override
    public Account debit(UUID id, double amount) {
        checkPositive(amount);
        Account account = findActive(id);
        if (account.getBalance() < amount) {
            throw new BusinessRuleException("Solde insuffisant sur le compte " + id);
        }
        account.setBalance(account.getBalance() - amount);
        return account;
    }

    @Override
    public Account credit(UUID id, double amount) {
        checkPositive(amount);
        Account account = findActive(id);
        account.setBalance(account.getBalance() + amount);
        return account;
    }

    @Override
    public void delete(UUID id) {
        accountRepository.delete(findById(id));
    }

    private Account findActive(UUID id) {
        Account account = findById(id);
        if (!account.isActive()) {
            throw new BusinessRuleException("Le compte " + id + " est bloqué");
        }
        return account;
    }

    private void checkPositive(double amount) {
        if (amount <= 0) {
            throw new BusinessRuleException("Le montant doit être strictement positif");
        }
    }
}