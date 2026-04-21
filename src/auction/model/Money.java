package auction.model;

import java.math.*;
import java.util.*;

public class Money {
    private final BigDecimal amount; // BigDecimal để lưu số chính xác

    public Money(double value){
        this.amount = BigDecimal.valueOf(value); // convert từ double -> BigDecimal
    }

    public Money(BigDecimal amount){
        this.amount = amount;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    // ====== BUSSINESS METHODS ======
    public Money add(Money other){ // Cộng tiền
        return new Money(this.amount.add(other.amount));
    }

    public Money multiply(double factor){ // Nhân tiền với hệ số factor
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor))); 
    }

    public boolean isGreaterThan(Money other){ // So sánh tiền đấu giá có lớn hơn hiện tại không
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThanOrEqual(Money other){ // So sánh tiền đấu giá với tiền hiện tại
        return this.amount.compareTo(other.amount) <= 0;
    }

    // ====== FORMAT ======
    @Override
    public String toString(){ // format tiền để in ra đẹp
        return amount.setScale(2, RoundingMode.HALF_UP).toString();
        // setScale (2,....) để giữ 2 chữ số sau dấu phẩy
        // RoundingMode.HALF_UP dùng để làm tròn kiểu toán học
    }

    // ====== EQUALS & HASHCODE ======

    @Override
    public boolean equals(Object o){ // So sánh 2 object có bằng nhau không
        if (this == o) return true; // Nếu có return true
        if (!(o instanceof Money)) return false; // Nếu không cùng kiểu return false

        Money money = (Money) o; // Ép kiểu về Money để dùng tiếp
        return amout.compareTo(money.amount) == 0; 
        // Kiểm tra xem số tiền của object hiện tại có bằng số tiền của object truyền vào không
        // Nếu bằng nhau trả true, nếu không trả false
    }

    @Override
    public int hashCode(){ // Tạo một con số đại diện cho object để dễ dàng tìm
        return amount.stripTrailingZeros().hashCode();
        // stripTrailingZeros() dùng để loại bỏ các số 0 dư ở cuối phần thập phân
    }
}
