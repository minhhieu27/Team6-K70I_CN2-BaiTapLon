package com.app.mapper;

import org.springframework.stereotype.Component;

import com.app.dto.response.wallet.WalletResponse;
import com.app.entity.wallet.Wallet;

@Component
public class WalletMapper {
    
    public WalletResponse toResponse(Wallet wallet){

        return WalletResponse.builder()
                .userId(wallet.getUser().getUserId())
                .balance(wallet.getBalance().getValue())
                .lockedBalance(wallet.getLockedAmount().getValue())
                .totalSpent(wallet.getTotalSpent().getValue())
                .build();
    }
}
