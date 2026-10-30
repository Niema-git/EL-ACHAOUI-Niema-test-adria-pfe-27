package org.sid.accountservice.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(UUID id) {
        super("Compte introuvable : " + id);
    }
}