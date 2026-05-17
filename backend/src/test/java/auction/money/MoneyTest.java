package auction.money;

import org.junit.jupiter.api.Test;

import com.app.common.money.Money;
import com.app.exception.validation.ValidationException;
import com.app.exception.wallet.InsufficientBalanceException;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {
    
    @Test
    void shoulCreateMoney(){
    Money m = new Money(100);
        assertEquals(100, m.getValue().doubleValue());
    }

    @Test
    void shouldAddMoney(){
        Money m1 = new Money(100);
        Money m2 = new Money(50);

        Money result = m1.add(m2);

        assertEquals(150, result.getValue().doubleValue());
    }

    @Test
    void shouldSubtractMoney(){
        Money m1 = new Money(100);
        Money m2 = new Money(30);

        Money result = m1.subtract(m2);

        assertEquals(70, result.getValue().doubleValue());
    }

    @Test
    void shouldMultipyMoney(){
        Money m1 = new Money(100);
        
        Money result = m1.multiply(1.2);

        assertEquals(120, result.getValue().doubleValue());
    }

    @Test
    void shouldNotAllowNegativwMoney(){
        assertThrows(ValidationException.class, ()-> new Money(-10));
    }

    @Test
    void shouldThrowWhenSubtractTooMuch(){
        Money m1 = new Money(50);
        Money m2 = new Money(100);

        assertThrows(InsufficientBalanceException.class,()-> m1.subtract(m2));
    }

    @Test
    void shouldThrowWhenMultipyNegative(){
        Money m = new Money(100);

        assertThrows(ValidationException.class, ()-> m.multiply(-2));
    }
}
