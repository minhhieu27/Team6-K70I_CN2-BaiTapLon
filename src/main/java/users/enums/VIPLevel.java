package users.enums;

import java.math.BigDecimal;
import auction.model.Money;;

public enum VIPLevel {
    NORMAL(new Money(BigDecimal.ZERO)),
    BRONZE(new Money(new BigDecimal("1000"))),
    SILVER(new Money(new BigDecimal("5000"))),
    GOLD(new Money(new BigDecimal("10000)"))),
    DIAMOND(new Money(new BigDecimal("50000")));

    private final Money requiredWithdraw;
    
    VIPLevel(Money requiredWithdraw){
        this.requiredWithdraw = requiredWithdraw;
    }

    public Money getRequiredWithdraw(){
        return requiredWithdraw;
    }
}
