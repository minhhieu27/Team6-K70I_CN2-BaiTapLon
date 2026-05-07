package com.app.domain.wallet;

import java.time.LocalDateTime;

import com.app.domain.enums.TransactionType;
import com.app.domain.model.Money;

public class Transaction {
    private final Money amount;
    private final TransactionType type;
    private final LocalDateTime time;
    private String userId;

    public Transaction(String userId, Money amount, TransactionType type){
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.time = LocalDateTime.now();
    }

    public String getUserId(){
        return userId;
    }

    public Money getAmount(){
        return amount;
    }

    public TransactionType getType(){
        return type;
    }

    public LocalDateTime getTime(){
        return time;
    }

    @Override
    public String toString(){
        return "Transaction{" +
        "userId: '" + userId + '\'' +
        ", amount = " + amount +
        ", type = " + type +
        ", time = " + time +
        '}';
    }
}
