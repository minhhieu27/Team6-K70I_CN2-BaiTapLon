package users.service;

import auction.model.Money;
import users.enums.Role;
import users.enums.VIPLevel;
import users.model.user.User;

public class WithdrawService {
    
    public void withdraw(User user, Money amount){
        if (user.isLocked()){
            throw new RuntimeException("Tài khoản bị khóa");
        }

        user.getWallet().withdraw(amount);

        if (user.hasRole(Role.BIDDER)){
            upgradeVIP(user);
        }
    }

    private void upgradeVIP(User user){

        Money total = user.getWallet().getTotalSpent();

        for (VIPLevel level : VIPLevel.values()){
            if (total.isGreaterThanOrEqual(level.getRequiredWithdraw())){
                user.setVIPLevel(level);
            }
        }
    }
}
