package org.sid.transactionservice.feign;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.sid.transactionservice.model.Account;
import org.sid.transactionservice.service.exception.AccountServiceUnavailableException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@FeignClient(name = "account-service", url = "${account-service.url}")
public interface AccountRestClient {

    @GetMapping("/api/accounts/{id}")
    @CircuitBreaker(name = "accountService", fallbackMethod = "getDefaultAccount")
    Account getAccountById(@PathVariable UUID id);

    @PostMapping("/api/accounts/{id}/debit")
    @CircuitBreaker(name = "accountService", fallbackMethod = "operationFallback")
    Account debit(@PathVariable UUID id, @RequestParam double amount);

    @PostMapping("/api/accounts/{id}/credit")
    @CircuitBreaker(name = "accountService", fallbackMethod = "operationFallback")
    Account credit(@PathVariable UUID id, @RequestParam double amount);

    // Consultation : on renvoie un compte "UNAVAILABLE", que le service refusera
    default Account getDefaultAccount(UUID id, Exception e) {
        rethrowBusinessError(e);
        return Account.builder().id(id).status("UNAVAILABLE").currency("N/A").balance(0).build();
    }

    // Débit / crédit : impossible d'inventer un résultat -> on signale l'indisponibilité
    default Account operationFallback(UUID id, double amount, Exception e) {
        rethrowBusinessError(e);
        throw new AccountServiceUnavailableException("account-service indisponible, réessayez plus tard");
    }

    // Une erreur métier (404 compte inexistant, 422 solde insuffisant...) n'est pas une panne
    private void rethrowBusinessError(Exception e) {
        if (e instanceof FeignException fe && fe.status() >= 400 && fe.status() < 500) {
            throw fe;
        }
    }
}