package auction.users;

import org.junit.jupiter.api.Test;

import auction.model.Money;
import users.enums.VIPLevel;
import users.model.user.User;
import users.model.user.UserAccount;
import users.service.DepositService;
import users.service.WithdrawService;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

public class UserTest {

    @Test
    void shouldCreateUser(){
        User user = new User("A00", new UserAccount("test@gmail.com", "abc123"));

        assertEquals("test@gmail.com", user.getAccount().getEmail());
    }

    @Test
    void shouldUpgradeLevel(){
        User user = new User("A01", new UserAccount("test@gmail.com", "abc123"));

        DepositService depositService = new DepositService();
        WithdrawService withdrawService = new WithdrawService();

        depositService.deposit(user, new Money(new BigDecimal("2000")));
        withdrawService.withdraw(user, new Money(new BigDecimal("1000")));

        assertEquals(VIPLevel.BRONZE, user.getVIPLevel());
    }
}