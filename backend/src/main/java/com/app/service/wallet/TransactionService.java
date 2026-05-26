package com.app.service.wallet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.app.common.enums.TransactionType;
import com.app.common.money.Money;
import com.app.dto.response.wallet.TransactionResponse;
import com.app.entity.wallet.Transaction;
import com.app.entity.wallet.Wallet;
import com.app.mapper.TransactionMapper;
import com.app.repository.TransactionRepository;

@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    public Page<TransactionResponse> getUserTransactions(String userId, int page, int size){

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());

        return transactionRepository.findByWallet_User_UserIdOrderByTimeDesc(userId, pageable).map(transactionMapper::toResponse);
    }

    public void createTransaction(Wallet wallet, Money amount, TransactionType type){

        Transaction transaction = Transaction.builder()
                                            .wallet(wallet)
                                            .amount(amount)
                                            .type(type)
                                            .build();

        transactionRepository.save(transaction);
    }
}
