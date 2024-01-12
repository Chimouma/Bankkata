package com.kata.bankaccount.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ClientAccountNotFoundException extends RuntimeException{
    public ClientAccountNotFoundException(final Long accountId) {
        super(format("Account id (%s) not found.", accountId));
    }
}
