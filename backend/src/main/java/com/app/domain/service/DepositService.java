package com.app.domain.service;

import com.app.domain.model.Money;
import com.app.domain.user.User;

public class DepositService {
    
    public void deposit(User user, Money amount){
        if (user.isLocked()){
            throw new RuntimeException("Tài khoản bị khóa");
        }

        user.getWallet().deposit(amount);
    }
}
