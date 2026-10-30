package org.sid.transactionservice.service.exception;

public class AccountServiceUnavailableException extends RuntimeException {
    public AccountServiceUnavailableException(String message) { super(message); }
}
