package com.kata.bankaccount;

import com.kata.bankaccount.entity.AccountEntity;
import com.kata.bankaccount.entity.OperationEntity;
import com.kata.bankaccount.repository.AccountRepository;
import com.kata.bankaccount.repository.OperationRepository;
import com.kata.bankaccount.service.AccountOperationsService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BankAccountOperationsIT {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    AccountOperationsService operationsService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_create_operation_when_balance_ok() throws Exception {
        //final File jsonFile = new ClassPathResource("/request/withdrawal-operation.json").getFile();
        //final String withdrawalOperation = Files.readString(jsonFile.toPath());
        AccountEntity account = AccountEntity.builder()
                .balance(1000l)
                .id(1l)
                .build();
        accountRepository.save(account);
        this.mockMvc.perform(
                        post("/api/v1/accounts/operations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content("{\n" +
                                        "  \"accountId\": 1,\n" +
                                        "  \"amount\": 50,\n" +
                                        "  \"email\": \"akevin@yahoo.fr\",\n" +
                                        "  \"timestamp\":  \"2019-07-17T10:15:30Z\",\n" +
                                        "  \"operationType\":\"WITHDRAWAL\"\n" +
                                        "}"))
                .andExpect(status().isCreated());
        List<OperationEntity> accountOperations = operationRepository.findAll();
        assertThat(accountOperations).isNotEmpty();
        assertThat(accountOperations.stream().findFirst().get().getAmount()).isEqualTo(50);
    }

    @Test
    void should_return_409_when_balance_is_not_sufficient() throws Exception {
        AccountEntity account = AccountEntity.builder()
                .balance(22l)
                .id(1l)
                .build();
        accountRepository.save(account);
        this.mockMvc.perform(
                        post("/api/v1/accounts/operations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content("{\n" +
                                        "  \"accountId\": 1,\n" +
                                        "  \"amount\": 50,\n" +
                                        "  \"email\": \"akevin@yahoo.fr\",\n" +
                                        "  \"timestamp\":  \"2019-07-17T10:15:30Z\",\n" +
                                        "  \"operationType\":\"WITHDRAWAL\"\n" +
                                        "}"))
                .andExpect(status().isConflict());
    }
}
