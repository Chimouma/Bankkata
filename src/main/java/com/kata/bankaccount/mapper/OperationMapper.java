package com.kata.bankaccount.mapper;

import com.kata.bankaccount.dto.AccountOperationsDto;
import com.kata.bankaccount.dto.OperationType;
import com.kata.bankaccount.entity.AccountEntity;
import com.kata.bankaccount.entity.OperationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
//@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public class OperationMapper {
    public OperationEntity from(AccountOperationsDto operationsDto){
        return OperationEntity.builder()
                .amount(operationsDto.getAmount())
                .timestamp(operationsDto.getTimestamp())
                .operationType(operationsDto.getOperationType().name())
                .build();
    }
    public AccountOperationsDto toEntity(OperationEntity operationEntity){
        return AccountOperationsDto.builder()
                .timestamp(operationEntity.getTimestamp())
                .accountId(operationEntity.getId())
                .operationType(OperationType.valueOf(operationEntity.getOperationType()))
                .amount(operationEntity.getAmount())
                .build();
    }
}
