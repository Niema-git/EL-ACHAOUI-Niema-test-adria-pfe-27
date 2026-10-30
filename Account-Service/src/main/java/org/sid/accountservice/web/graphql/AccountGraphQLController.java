package org.sid.accountservice.web.graphql;

import lombok.RequiredArgsConstructor;
import org.sid.accountservice.domain.Account;
import org.sid.accountservice.domain.AccountStatus;
import org.sid.accountservice.service.AccountService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AccountGraphQLController {

    private final AccountService accountService;

    @QueryMapping
    public List<Account> accounts() {
        return accountService.findAll();
    }

    @QueryMapping
    public Account accountById(@Argument UUID id) {
        return accountService.findById(id);
    }

    @QueryMapping
    public List<Account> accountsByCustomer(@Argument String customerReference) {
        return accountService.findByCustomer(customerReference);
    }

    @MutationMapping
    public Account createAccount(@Argument Account input) {
        return accountService.create(input);
    }

    @MutationMapping
    public Account updateAccountStatus(@Argument UUID id, @Argument AccountStatus status) {
        return accountService.updateStatus(id, status);
    }

    @MutationMapping
    public Account debitAccount(@Argument UUID id, @Argument double amount) {
        return accountService.debit(id, amount);
    }

    @MutationMapping
    public Account creditAccount(@Argument UUID id, @Argument double amount) {
        return accountService.credit(id, amount);
    }

    @MutationMapping
    public boolean deleteAccount(@Argument UUID id) {
        accountService.delete(id);
        return true;
    }
}