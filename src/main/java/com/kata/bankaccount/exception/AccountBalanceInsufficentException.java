package com.kata.bankaccount.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class AccountBalanceInsufficentException extends RuntimeException {
    public AccountBalanceInsufficentException(){
        super("Account doesn't have enough balance for withdrawal.");
    }
}
