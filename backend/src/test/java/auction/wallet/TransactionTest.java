package auction.wallet;

import org.junit.jupiter.api.Test;

import com.app.common.enums.TransactionType;
import com.app.common.money.Money;
import com.app.entity.Transaction;
import com.app.entity.UserEntity;
import com.app.entity.Wallet;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {
    
    // ====== DEPOSIT ======
    @Test
    void shouldCreateDepositTransaction(){
        UserEntity user = new UserEntity("abc", "123@gmail.com", "123456");
        Wallet wallet = user.getWallet();

        Transaction t = new Transaction(new Money(100), TransactionType.DEPOSIT, wallet);

        assertEquals(TransactionType.DEPOSIT, t.getType());
        assertEquals(wallet, t.getWallet());
        assertNotNull(t.getTime());
    }

    // ====== WITHDRAW ======
    @Test
    void shouldCreateWithdrawTransaction(){
        UserEntity user = new UserEntity("abc", "123@gmail.com", "123456");
        Wallet wallet = user.getWallet();

        Transaction t = new Transaction(new Money(500), TransactionType.WITHDRAW, wallet);

        assertEquals(TransactionType.WITHDRAW, t.getType());
        assertEquals(wallet, t.getWallet());
        assertNotNull(t.getTime());
    }
}