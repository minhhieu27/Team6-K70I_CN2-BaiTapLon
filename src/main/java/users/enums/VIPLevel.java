package users.enums;

import auction.model.Money;

public enum VIPLevel {
    NORMAL(new Money(0.0)),
    BRONZE(new Money(100.0)),
    SILVER(new Money(1000.0)),
    GOLD(new Money(5000.0)),
    DIAMOND(new Money(10000.0));

    private final Money requiredDeposit;

    VIPLevel(Money requiredDeposit){
        this.requiredDeposit = requiredDeposit;
    }

    public Money getRequiredDeposit(){
        return requiredDeposit;
    }
}
