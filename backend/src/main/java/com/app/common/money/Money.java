package com.app.common.money;

import java.math.*;

import com.app.exception.validation.ValidationException;
import com.app.exception.wallet.InsufficientBalanceException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class Money {

    @Column(precision = 19, scale = 2)
    private BigDecimal value; // BigDecimal để lưu số chính xác

    public Money(BigDecimal value){
        validate(value);

        this.value = value;
    }

    public Money(double value1){
        BigDecimal bd = BigDecimal.valueOf(value1); // convert từ double sang bigdecimal
        validate(bd);
        this.value = bd;
    }

    public void validate(BigDecimal value){
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0){
            throw new ValidationException("Số tiền phải lớn hơn 0");
        }
    }

    public BigDecimal getValue(){
        return value;
    }

    // ====== BUSSINESS METHODS ======
    public Money add(Money other){ // Cộng tiền
        return new Money(this.value.add(other.value));
    }

    public Money subtract(Money other){ // Trừ tiền
        if (this.value.compareTo(other.value) < 0){ throw new InsufficientBalanceException("Số dư không đủ");}

        return new Money(this.value.subtract(other.value));
    }

    public Money multiply(double factor){ // Nhân tiền với hệ số factor
        return new Money(this.value.multiply(BigDecimal.valueOf(factor))); 
    }

    public int compareTo(Money other){
        return this.value.compareTo(other.value);
    }

    public boolean isGreaterThan(Money other){ // So sánh tiền đấu giá có lớn hơn hiện tại không
        return this.compareTo(other) > 0;
    }

    public boolean isEqual(Money other){
        return this.compareTo(other) == 0;
    }

    // ====== EQUALS & HASHCODE ======

    @Override
    public boolean equals(Object o){ // So sánh 2 object có bằng nhau không
        if (this == o) return true; // Nếu có return true
        if (!(o instanceof Money)) return false; // Nếu không cùng kiểu return false

        Money money = (Money) o; // Ép kiểu về Money để dùng tiếp
        return value.compareTo(money.value) == 0; 
        // Kiểm tra xem số tiền của object hiện tại có bằng số tiền của object truyền vào không
        // Nếu bằng nhau trả true, nếu không trả false
    }

    @Override 
    public int hashCode(){ // Tạo một con số đại diện cho object để dễ dàng tìm
        return value.stripTrailingZeros().hashCode();
        // stripTrailingZeros() dùng để loại bỏ các số 0 dư ở cuối phần thập phân
    }

    public static Money isZero() {
        return new Money(BigDecimal.ZERO);
    }
}