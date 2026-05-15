package com.app.event;

import com.app.common.money.Money;

public class DepositEvent implements Event {
    private final String userId;
    private final Money amount;

    public DepositEvent(String userId, Money amount){
        this.userId = userId;
        this.amount = amount;
    }

    public String getUserId(){
        return userId;
    }

    public Money getAmount(){
        return amount;
    }
}
