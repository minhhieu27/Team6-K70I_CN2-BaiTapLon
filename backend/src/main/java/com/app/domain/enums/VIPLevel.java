package com.app.domain.enums;

import java.math.BigDecimal;

import com.app.domain.model.Money;;

public enum VIPLevel {
    NORMAL(BigDecimal.valueOf(0)),
    BRONZE(BigDecimal.valueOf(1000)),
    SILVER(BigDecimal.valueOf(5000)),
    GOLD(BigDecimal.valueOf(10000)),
    DIAMOND(BigDecimal.valueOf(50000));

    private final BigDecimal requiredWithdraw;
    
    VIPLevel(BigDecimal requiredWithdraw){
        this.requiredWithdraw = requiredWithdraw;
    }

    public Money getRequiredWithdraw(){
        return new Money(requiredWithdraw);
    }
}
