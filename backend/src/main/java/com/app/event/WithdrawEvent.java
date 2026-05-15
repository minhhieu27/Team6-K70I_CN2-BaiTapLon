package com.app.event;

import com.app.common.money.Money;

public class WithdrawEvent implements Event {
    private final String userId;
    private final Money amount;

    public WithdrawEvent(String userId, Money amount){
        this.userId = userId;
        this.amount = amount;
    }
    
    public String getUserId() {return userId; }

    public Money getAmount() {return amount; }
}
