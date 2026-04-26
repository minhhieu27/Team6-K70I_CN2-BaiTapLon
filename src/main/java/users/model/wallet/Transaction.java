package users.model.wallet;

import java.time.LocalDateTime;

import auction.model.Money;
import users.enums.TransactionType;

public class Transaction {
    private final Money amount;
    private final TransactionType type;
    private final LocalDateTime time;

    public Transaction(Money amount, TransactionType type){
        this.amount = amount;
        this.type = type;
        this.time = LocalDateTime.now();
    }
}
