package users.model.wallet;

import java.time.LocalDateTime;

import auction.model.Money;
import users.enums.TransactionType;

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
