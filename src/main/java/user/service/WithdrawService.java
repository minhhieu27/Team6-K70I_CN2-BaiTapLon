package user.service;

import user.model.user.User;

public class WithdrawService {
    
    public void withdraw(User user, Money amount){
        if (!user.isActive()){
            throw new RuntimeException("Tài khoản bị khóa");
        }

        user.getWallet().withdraw(amout);
    }
}
