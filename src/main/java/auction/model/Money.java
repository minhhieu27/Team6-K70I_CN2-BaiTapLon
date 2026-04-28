package auction.model;

import java.math.*;

public class Money {
    private final BigDecimal amount; // BigDecimal để lưu số chính xác

    public Money(BigDecimal amount){
        validate(amount);

        this.amount = amount;
    }

    public Money(double value){
        BigDecimal bd = BigDecimal.valueOf(value); // convert từ double sang bigdecimal
        validate(bd);
        this.amount = bd;
    }

    public void validate(BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0");
        }
    }

    public BigDecimal getAmount(){
        return amount;
    }

    // ====== BUSSINESS METHODS ======
    public Money add(Money other){ // Cộng tiền
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other){ // Trừ tiền
        if (this.amount.compareTo(other.amount) < 0) throw new IllegalArgumentException("Số dư không đủ");

        return new Money(this.amount.subtract(other.amount));
    }

    public Money multiply(double factor){ // Nhân tiền với hệ số factor
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor))); 
    }

    public int compareTo(Money other){
        return this.amount.compareTo(other.amount);
    }

    public boolean isGreaterThanOrEqual(Money other){ // So sánh tiền đấu giá có lớn hơn hiện tại không
        return this.compareTo(other) >= 0;
    }

    public boolean isLessThan(Money other){ // So sánh tiền đấu giá với tiền hiện tại
        return this.compareTo(other) < 0;
    }

    // ====== EQUALS & HASHCODE ======

    @Override
    public boolean equals(Object o){ // So sánh 2 object có bằng nhau không
        if (this == o) return true; // Nếu có return true
        if (!(o instanceof Money)) return false; // Nếu không cùng kiểu return false

        Money money = (Money) o; // Ép kiểu về Money để dùng tiếp
        return amount.compareTo(money.amount) == 0; 
        // Kiểm tra xem số tiền của object hiện tại có bằng số tiền của object truyền vào không
        // Nếu bằng nhau trả true, nếu không trả false
    }

    @Override 
    public int hashCode(){ // Tạo một con số đại diện cho object để dễ dàng tìm
        return amount.stripTrailingZeros().hashCode();
        // stripTrailingZeros() dùng để loại bỏ các số 0 dư ở cuối phần thập phân
    }

    @Override
    public String toString(){
        return amount.toString();
    }

    public boolean isZero() {
        throw new UnsupportedOperationException("Unimplemented method 'isZero'");
    }
}