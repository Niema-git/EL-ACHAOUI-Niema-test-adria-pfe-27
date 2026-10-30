package org.sid.transactionservice.service;

import org.sid.transactionservice.domain.Transaction;
import org.sid.transactionservice.domain.TransactionType;
import org.sid.transactionservice.feign.AccountRestClient;
import org.sid.transactionservice.model.Account;
import org.sid.transactionservice.repository.TransactionRepository;
import org.sid.transactionservice.service.exception.BusinessRuleException;
import org.sid.transactionservice.service.exception.TransactionNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRestClient accountRestClient;

    @Value("${transaction.max-amount}")
    private BigDecimal maxAmount;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  AccountRestClient accountRestClient) {
        this.transactionRepository = transactionRepository;
        this.accountRestClient = accountRestClient;
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction findById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    @Override
    public List<Transaction> findByAccount(UUID accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    public Transaction create(Transaction transaction) {
        checkAmount(transaction.getAmount());
        Account account = accountRestClient.getAccountById(transaction.getAccountId()); // consultation du solde
        checkAccount(account, transaction.getCurrency(), transaction.getAmount());

        transaction.setId(null);
        transaction.setCreatedAt(Instant.now());
        transaction.setType(TransactionType.PAYMENT);
        Transaction saved = transactionRepository.save(transaction);

        accountRestClient.debit(account.getId(), transaction.getAmount().doubleValue()); // mise à jour du solde
        return saved;
    }

    @Override
    public List<Transaction> transfer(UUID sourceId, UUID targetId, BigDecimal amount, String currency) {
        if (sourceId.equals(targetId)) {
            throw new BusinessRuleException("Le compte source et le compte cible doivent être différents");
        }
        checkAmount(amount);
        Account source = accountRestClient.getAccountById(sourceId);
        checkAccount(source, currency, amount);
        Account target = accountRestClient.getAccountById(targetId);
        checkAccount(target, currency, null);

        Instant now = Instant.now();
        Transaction out = transactionRepository.save(Transaction.builder()
                .accountId(sourceId).amount(amount).currency(currency.toUpperCase())
                .merchantCategory("TRANSFER").createdAt(now).type(TransactionType.TRANSFER_OUT).build());
        Transaction in = transactionRepository.save(Transaction.builder()
                .accountId(targetId).amount(amount).currency(currency.toUpperCase())
                .merchantCategory("TRANSFER").createdAt(now).type(TransactionType.TRANSFER_IN).build());

        accountRestClient.debit(sourceId, amount.doubleValue());
        try {
            accountRestClient.credit(targetId, amount.doubleValue());
        } catch (Exception e) {
            accountRestClient.credit(sourceId, amount.doubleValue()); // compensation : on rend l'argent
            throw new BusinessRuleException("Virement annulé : crédit du compte cible impossible");
        }
        return List.of(out, in);
    }

    private void checkAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Le montant doit être strictement positif");
        }
        if (amount.compareTo(maxAmount) > 0) {
            throw new BusinessRuleException("Le montant dépasse le plafond autorisé (" + maxAmount + ")");
        }
    }

    // amountToDebit = null quand le compte est seulement crédité
    private void checkAccount(Account account, String currency, BigDecimal amountToDebit) {
        if (account == null || account.getId() == null) {
            throw new BusinessRuleException("Le compte n'existe pas");
        }
        if (!"ACTIVE".equals(account.getStatus())) {
            throw new BusinessRuleException("Le compte " + account.getId() + " n'est pas actif");
        }
        if (!account.getCurrency().equalsIgnoreCase(currency)) {
            throw new BusinessRuleException("La devise " + currency + " ne correspond pas à celle du compte ("
                    + account.getCurrency() + ")");
        }
        if (amountToDebit != null && BigDecimal.valueOf(account.getBalance()).compareTo(amountToDebit) < 0) {
            throw new BusinessRuleException("Solde insuffisant (solde : " + account.getBalance() + ")");
        }
    }
}