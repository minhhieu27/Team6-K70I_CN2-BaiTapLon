package auction.users;

import org.junit.jupiter.api.Test;

import com.app.domain.user.User;
import com.app.domain.user.UserAccount;
import com.app.domain.user.VIPLevel;
import com.app.domain.wallet.Money;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

public class UserTest {

    @Test
    void shouldCreateUser(){
        User user = new User(new UserAccount("test@gmail.com", "abc123"));

        assertEquals("test@gmail.com", user.getAccount().getEmail());
    }

    @Test
    void shouldUpgradeLevel(){
        User user = new User(new UserAccount("test@gmail.com", "abc123"));

        user.deposit(new Money(new BigDecimal("2000")));
        user.withdraw(new Money(new BigDecimal("1000")));

        assertEquals(VIPLevel.BRONZE, user.getVIPLevel());
    }
}