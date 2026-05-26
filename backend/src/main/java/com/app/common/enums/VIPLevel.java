package com.app.common.enums;

import java.math.BigDecimal;

import com.app.common.money.Money;;

public enum VIPLevel {
    NORMAL(BigDecimal.valueOf(0), 0),
    BRONZE(BigDecimal.valueOf(5000000), 3),
    SILVER(BigDecimal.valueOf(50000000), 7),
    GOLD(BigDecimal.valueOf(500000000), 12),
    DIAMOND(BigDecimal.valueOf(1000000000), 18);

    private final BigDecimal requiredWithdraw;

    private final int discountPercent;
    
    VIPLevel(BigDecimal requiredWithdraw, int discountPercent){
        this.requiredWithdraw = requiredWithdraw;
        this.discountPercent = discountPercent;
    }

    public Money getRequiredWithdraw(){
        return new Money(requiredWithdraw);
    }

    public int getDiscountPercent(){
        return discountPercent;
    }
}
