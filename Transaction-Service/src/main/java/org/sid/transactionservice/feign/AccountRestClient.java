package org.sid.transactionservice.feign;

import org.sid.transactionservice.model.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@FeignClient(name = "account-service", url = "${account-service.url}")
public interface AccountRestClient {

    @GetMapping("/api/accounts/{id}")
    Account getAccountById(@PathVariable UUID id);

    @PostMapping("/api/accounts/{id}/debit")
    Account debit(@PathVariable UUID id, @RequestParam double amount);

    @PostMapping("/api/accounts/{id}/credit")
    Account credit(@PathVariable UUID id, @RequestParam double amount);
}