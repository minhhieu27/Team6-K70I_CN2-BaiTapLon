package auction.wallet;

import org.junit.jupiter.api.Test;

import com.app.domain.model.Money;
import com.app.domain.wallet.Wallet;

import static org.junit.jupiter.api.Assertions.*;

public class WalletTest {

    @Test
    void depositShouldIncreaseBalance(){
        Wallet wallet = new Wallet();

        wallet.deposit(new Money(200));

        assertEquals(200, wallet.getBalance().getAmount().doubleValue());
    }

    @Test
    void withdrawShouldDecreaseBalance(){
        Wallet wallet = new Wallet();

        wallet.deposit(new Money(200));
        wallet.withdraw(new Money(150));

        assertEquals(50, wallet.getBalance().getAmount().doubleValue());
    }

    @Test
    void withdrawShouldFailWhenNotEnoughMoney(){
        Wallet wallet = new Wallet();

        wallet.deposit(new Money(50));

        assertThrows(IllegalArgumentException.class, ()-> wallet.withdraw(new Money(100)));
        // ()-> tạo 1 hàm không có tham số, khi gọi thì chạy wallet.withdraw()
    }
}
