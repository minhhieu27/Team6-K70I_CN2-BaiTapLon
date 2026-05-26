package com.app.mapper;

import org.springframework.stereotype.Component;

import com.app.dto.response.wallet.TransactionResponse;
import com.app.entity.wallet.Transaction;

@Component
public class TransactionMapper {
    
    public TransactionResponse toResponse(Transaction transaction){

        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .amount(transaction.getAmount().getValue())
                .transactionType(transaction.getType())
                .createAt(transaction.getTime())
                .build();
    }
}
