package com.app.domain.service;

import com.app.domain.enums.VIPLevel;
import com.app.domain.model.Money;
import com.app.domain.user.User;

public class WithdrawService {
    
    public void withdraw(User user, Money amount){
        if (user.isLocked()){
            throw new RuntimeException("Tài khoản bị khóa");
        }

        user.getWallet().withdraw(amount);

       upgradeVIP(user);
    }

    private void upgradeVIP(User user){

        Money total = user.getWallet().getTotalSpent();

        VIPLevel newLevel = VIPLevel.NORMAL;

        for (VIPLevel level : VIPLevel.values()){
            if (total.isGreaterThanOrEqual(level.getRequiredWithdraw())){
                newLevel = level;
            }
        }

        user.setVIPLevel(newLevel);
    }
}
