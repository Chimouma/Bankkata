package com.kata.bankaccount.exception;

import com.kata.bankaccount.dto.OperationType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class AccountOperationNotSupportedException extends RuntimeException{
    public AccountOperationNotSupportedException(OperationType operationType) {
        super(format("Operation %s is not supported yet", operationType));
    }
}
