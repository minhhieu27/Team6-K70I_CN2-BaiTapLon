package auction.model;

import org.junit.jupiter.api.Test;

import com.app.domain.model.Money;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {
    
    @Test
    void shoulCreateMoney(){
    Money m = new Money(100);
        assertEquals(100, m.getAmount().doubleValue());
    }

    @Test
    void shouldAddMoney(){
        Money m1 = new Money(100);
        Money m2 = new Money(50);

        Money result = m1.add(m2);

        assertEquals(150, result.getAmount().doubleValue());
    }

    @Test
    void shouldSubtractMoney(){
        Money m1 = new Money(100);
        Money m2 = new Money(30);

        Money result = m1.subtract(m2);

        assertEquals(70, result.getAmount().doubleValue());
    }

    @Test
    void shouldMultipyMoney(){
        Money m1 = new Money(100);
        
        Money result = m1.multiply(1.2);

        assertEquals(120, result.getAmount().doubleValue());
    }

    @Test
    void shouldNotAllowNegativwMoney(){
        assertThrows(IllegalArgumentException.class, ()-> new Money(-10));
    }

    @Test
    void shouldThrowWhenSubtractTooMuch(){
        Money m1 = new Money(50);
        Money m2 = new Money(100);

        assertThrows(IllegalArgumentException.class,()-> m1.subtract(m2));
    }

    @Test
    void shouldThrowWhenMultipyNegative(){
        Money m = new Money(100);

        assertThrows(IllegalArgumentException.class, ()-> m.multiply(-2));
    }
}
