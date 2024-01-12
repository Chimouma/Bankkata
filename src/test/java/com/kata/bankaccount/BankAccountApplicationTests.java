package com.kata.bankaccount;

import com.kata.bankaccount.dto.AccountOperationsDto;
import com.kata.bankaccount.dto.OperationType;
import com.kata.bankaccount.entity.AccountEntity;
import com.kata.bankaccount.entity.OperationEntity;
import com.kata.bankaccount.exception.AccountBalanceInsufficentException;
import com.kata.bankaccount.exception.ClientAccountNotFoundException;
import com.kata.bankaccount.mapper.OperationMapper;
import com.kata.bankaccount.repository.AccountRepository;
import com.kata.bankaccount.repository.OperationRepository;
import com.kata.bankaccount.service.AccountOperationsService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.kata.bankaccount.dto.OperationType.WITHDRAWAL;
import static com.kata.bankaccount.entity.AccountOperationStatus.OPERATION_SUCCESSFUL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BankAccountApplication.class})
class BankAccountApplicationTests {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private OperationMapper operationMapper;
    @Autowired
    private AccountOperationsService accountOperationsService;


    @Test
    void should_throw_exception_when_account_not_found() {
        //Given
        AccountOperationsDto withdrawalOperation = AccountOperationsDto.builder()
                .accountId(1l)
                .amount(50l)
                .operationType(WITHDRAWAL)
                .timestamp(new Date())
                .build();
        //When
        Throwable throwable = catchThrowable(() -> accountOperationsService.executeOperation(withdrawalOperation));
        //Then
        assertThat(throwable).isExactlyInstanceOf(ClientAccountNotFoundException.class);
        assertThat(throwable).hasMessage("Account id (1) not found.");
    }

    @Test
    void should_withdraw_money_when_balance_ok() {
        //Given
        AccountEntity account = AccountEntity.builder()
                .balance(1000l)
                .id(1l)
                .build();
        accountRepository.save(account);
        Date withdrawalTime = new Date();
        AccountOperationsDto withdrawalOperation = AccountOperationsDto.builder()
                .accountId(1l)
                .amount(50l)
                .operationType(WITHDRAWAL)
                .timestamp(withdrawalTime)
                .build();
        //When
        accountOperationsService.executeOperation(withdrawalOperation);
        List<OperationEntity> accountOperations = operationRepository.findAll();
        Optional<AccountEntity> updateAccount = accountRepository.findById(1l);

        //Then
        assertFalse(accountOperations.isEmpty());
        Assert.assertEquals(accountOperations.size(), 1);
        OperationEntity savedWithdrawalOperation = accountOperations.stream().findFirst().get();
        Assert.assertEquals(savedWithdrawalOperation.getOperationType(), WITHDRAWAL.name());
        Assert.assertEquals(savedWithdrawalOperation.getStatus(), OPERATION_SUCCESSFUL.name());
        Assert.assertEquals(savedWithdrawalOperation.getAmount().longValue(), 50l);
        Assert.assertEquals(savedWithdrawalOperation.getTimestamp().toInstant(), withdrawalTime.toInstant());
        Assert.assertEquals(updateAccount.get().getBalance().longValue(), 950l);
    }

    @Test
    void should_Throw_exception_when_balance_is_not_sufficient() {
        //Given
        AccountEntity account = AccountEntity.builder()
                .balance(20l)
                .id(1l)
                .build();
        accountRepository.save(account);
        AccountOperationsDto withdrawalOperation = AccountOperationsDto.builder()
                .accountId(1l)
                .amount(50l)
                .operationType(WITHDRAWAL)
                .timestamp(new Date())
                .build();
        //When
        Throwable throwable = catchThrowable(() -> accountOperationsService.executeOperation(withdrawalOperation));
        //Then
        assertThat(throwable).isExactlyInstanceOf(AccountBalanceInsufficentException.class);
        assertThat(throwable).hasMessage("Account doesn't have enough balance for withdrawal.");
    }
}
