package com.app.mapper;

import org.springframework.stereotype.Component;

import com.app.dto.response.WalletResponse;
import com.app.entity.Wallet;

@Component
public class WalletMapper {
    
    public WalletResponse toResponse(Wallet wallet){

        return WalletResponse.builder()
                .userId(wallet.getUser().getUserId())
                .balance(wallet.getBalance().getAmount())
                .lockedBalance(wallet.getLockedAmount().getAmount())
                .build();
    }
}
