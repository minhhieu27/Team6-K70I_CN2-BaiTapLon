package auction.users;

import org.junit.jupiter.api.Test;

import com.app.domain.enums.VIPLevel;
import com.app.domain.model.Money;
import com.app.domain.service.DepositService;
import com.app.domain.service.WithdrawService;
import com.app.domain.user.User;
import com.app.domain.user.UserAccount;

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