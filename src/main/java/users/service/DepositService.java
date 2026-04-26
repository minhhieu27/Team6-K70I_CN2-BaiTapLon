package users.service;

import auction.model.Money;
import users.enums.*;
import users.model.user.User;

public class DepositService {
    
    public void deposit(User user, Money amount){
        if (!user.isActive()){
            throw new RuntimeException("Tài khoản bị khóa");
        }

        user.getWallet().deposit(amount);

        if (user.hasRole(Role.BIDDER)) {
            upgradeVIP(user);
        }
    }

    private void upgradeVIP(User user){

        Money total = user.getWallet().getTotalDeposit();

        for (VIPLevel level : VIPLevel.values()){
            if (total >= level.getRequiredDeposit()){
                user.setVIPLevel(level);
            }
        }
    }
}
