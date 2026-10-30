package org.sid.transactionservice.web.rest;

import feign.FeignException;
import org.sid.transactionservice.service.exception.AccountServiceUnavailableException;
import org.sid.transactionservice.service.exception.BusinessRuleException;
import org.sid.transactionservice.service.exception.TransactionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(TransactionNotFoundException.class)
    public ProblemDetail handleNotFound(TransactionNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleBusinessRule(BusinessRuleException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), e.getMessage());
    }

    // Erreur renvoyée par account-service (ex : compte inexistant -> 404)
    @ExceptionHandler(FeignException.class)
    public ProblemDetail handleFeign(FeignException e) {
        int status = e.status() > 0 ? e.status() : 503;
        String detail = status == 404 ? "Le compte n'existe pas" : "Erreur account-service : " + e.getMessage();
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(status), detail);
    }
    @ExceptionHandler(AccountServiceUnavailableException.class)
    public ProblemDetail handleUnavailable(AccountServiceUnavailableException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }
}