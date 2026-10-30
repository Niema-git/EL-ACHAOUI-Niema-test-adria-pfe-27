package org.sid.accountservice.mcp;

import org.sid.accountservice.domain.Account;
import org.sid.accountservice.domain.AccountStatus;
import org.sid.accountservice.service.AccountService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

@Component
public class AccountMcpTools {

    private final AccountService accountService;

    public AccountMcpTools(AccountService accountService) {
        this.accountService = accountService;
    }

    @McpTool(description = "Get all bank accounts")
    public List<Account> getAllAccounts() {
        return accountService.findAll();
    }

    @McpTool(description = "Get a bank account by id")
    public Account getAccountById(@McpToolParam(description = "The account id (UUID)") String id) {
        return accountService.findById(UUID.fromString(id));
    }

    @McpTool(description = "Get the bank accounts of a customer")
    public List<Account> getAccountsByCustomer(@McpToolParam(description = "The customer reference") String customerReference) {
        return accountService.findByCustomer(customerReference);
    }

    @McpTool(description = "Save a new bank account")
    public Account saveAccount(@McpToolParam(description = "The account to save (customerReference, currency, balance)") Account account) {
        return accountService.create(account);
    }

    @McpTool(description = "Change the status of a bank account")
    public Account updateStatus(@McpToolParam(description = "The account id (UUID)") String id,
                                @McpToolParam(description = "ACTIVE or BLOCKED") AccountStatus status) {
        return accountService.updateStatus(UUID.fromString(id), status);
    }
}