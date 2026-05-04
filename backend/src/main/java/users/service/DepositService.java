package users.service;

import auction.model.Money;
import users.model.user.User;

public class DepositService {
    
    public void deposit(User user, Money amount){
        if (user.isLocked()){
            throw new RuntimeException("Tài khoản bị khóa");
        }

        user.getWallet().deposit(amount);
    }
}
