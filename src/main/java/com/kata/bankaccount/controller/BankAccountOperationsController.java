package com.kata.bankaccount.controller;

import com.kata.bankaccount.dto.AccountOperationsDto;
import com.kata.bankaccount.service.AccountOperationsService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "api/v1/operations", produces = APPLICATION_JSON_VALUE)
@Slf4j
public class BankAccountOperationsController {


    private final AccountOperationsService operationService;

    public BankAccountOperationsController(AccountOperationsService operationService) {
        this.operationService = operationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void executeOperation(@RequestBody @NonNull AccountOperationsDto operationDto) {
        operationService.executeOperation(operationDto);
    }

}
