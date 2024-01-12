package com.kata.bankaccount.service;

import com.kata.bankaccount.dto.AccountOperationsDto;
import com.kata.bankaccount.entity.OperationEntity;
import com.kata.bankaccount.exception.AccountBalanceInsufficentException;
import com.kata.bankaccount.exception.AccountOperationNotSupportedException;
import com.kata.bankaccount.exception.ClientAccountNotFoundException;
import com.kata.bankaccount.mapper.OperationMapper;
import com.kata.bankaccount.repository.AccountRepository;
import com.kata.bankaccount.repository.OperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kata.bankaccount.entity.AccountOperationStatus.OPERATION_FAILED;
import static com.kata.bankaccount.entity.AccountOperationStatus.OPERATION_SUCCESSFUL;
import static com.kata.bankaccount.dto.OperationType.DEPOSIT;
import static com.kata.bankaccount.dto.OperationType.WITHDRAWAL;

@Service
public class AccountOperationsService {
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    private final OperationMapper operationMapper;

    public AccountOperationsService(final AccountRepository accountRepository,
                                    final OperationRepository operationRepository,
                                    final OperationMapper operationMapper) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
        this.operationMapper = operationMapper;
    }

    @Transactional
    public void executeOperation(AccountOperationsDto operationDto) {
        accountRepository.findById(operationDto.getAccountId()).ifPresentOrElse(accountEntity -> {
            if (WITHDRAWAL.equals(operationDto.getOperationType()) && accountEntity.getBalance() >= operationDto.getAmount()) {
                Long newBalance = accountEntity.getBalance() - operationDto.getAmount();
                accountEntity.setBalance(newBalance);
                accountRepository.save(accountEntity);
                OperationEntity newOperation = operationMapper.from(operationDto);
                newOperation.setStatus(OPERATION_SUCCESSFUL.name());
                operationRepository.save(newOperation);
            } else if (WITHDRAWAL.equals(operationDto.getOperationType())) {
                OperationEntity newOperation = operationMapper.from(operationDto);
                newOperation.setStatus(OPERATION_FAILED.name());
                operationRepository.save(newOperation);
                throw new AccountBalanceInsufficentException();
            } else if(DEPOSIT.equals(operationDto.getOperationType())){
                Long newBalance = accountEntity.getBalance() + operationDto.getAmount();
                accountEntity.setBalance(newBalance);
                accountRepository.save(accountEntity);
                OperationEntity newOperation = operationMapper.from(operationDto);
                newOperation.setStatus(OPERATION_SUCCESSFUL.name());
                operationRepository.save(newOperation);
            } else {
                throw new AccountOperationNotSupportedException(operationDto.getOperationType());
            }
        }, ()-> {
            throw new ClientAccountNotFoundException(operationDto.getAccountId());
        });
    }
}
