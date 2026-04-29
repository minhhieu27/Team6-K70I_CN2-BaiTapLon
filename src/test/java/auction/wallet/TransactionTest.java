package auction.wallet;

import org.junit.jupiter.api.Test;

import auction.model.Money;
import users.enums.TransactionType;
import users.model.wallet.Transaction;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {
    
    @Test
    void shouldCreateTransaction(){
        Transaction t = new Transaction("A01", new Money(100), TransactionType.DEPOSIT);

        assertEquals(0, t.getAmount().compareTo(new Money(100)));
        assertEquals(TransactionType.DEPOSIT, t.getType());
        assertNotNull(t.getTime());
    }
}