package com.kata.bankaccount.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Date;

@Value
@Builder
public class AccountOperationsDto {
    private Long accountId;
    private OperationType operationType;
    private Long amount;
    private Date timestamp;
}
