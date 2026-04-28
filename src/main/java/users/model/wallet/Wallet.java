package users.model.wallet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import auction.model.Money;
import users.enums.TransactionType;

public class Wallet {
    private Money balance = new Money(BigDecimal.ZERO);
    private Money totalDeposit = new Money(BigDecimal.ZERO);
    private Money lockedAmount = new Money(BigDecimal.ZERO);
    private String userId;
    private Money totalSpent = new Money(0);

    private List<Transaction> transactions = new ArrayList<>();

    public void deposit(Money amount){
        validate(amount);
        balance = balance.add(amount);
        totalDeposit = totalDeposit.add(amount);

        transactions.add(new Transaction(userId, amount, TransactionType.DEPOSIT));
    }

    public void withdraw(Money amount){
        validate(amount);
       
        balance = balance.subtract(amount);
        totalSpent = totalSpent.add(amount);
        transactions.add(new Transaction(userId, amount, TransactionType.WITHDRAW));
    }

    public void lock(Money amount){ // Khóa khi bid
        validate(amount);

        if (amount.isGreaterThanOrEqual(balance)) throw new IllegalArgumentException("Không đủ tiền");

        balance = balance.subtract(amount);
        lockedAmount = lockedAmount.add(amount);
    }

    public void unlock(Money amount) { // Unlock khi outbid
        lockedAmount = lockedAmount.subtract(amount);
        balance = balance.add(amount);
    }

    private void validate(Money amount){
        if (amount == null || amount.isZero()) throw new IllegalArgumentException("Số tiền không hợp lệ");
    }

    public Money getBalance(){
        return balance;
    }

    public Money getTotalDeposit(){
        return totalDeposit;
    }

    public Money getLockedAmount(){
        return lockedAmount;
    }
}
