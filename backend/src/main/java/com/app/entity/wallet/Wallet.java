package com.app.entity.wallet;

import java.util.ArrayList;
import java.util.List;

import com.app.common.money.Money;
import com.app.entity.user.UserEntity;
import com.app.exception.validation.ValidationException;
import com.app.exception.wallet.InsufficientBalanceException;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table (name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(
            name = "balance",
            nullable = false))
    private Money balance = new Money(0);

    @Embedded
    @AttributeOverride(name = "value", column = @Column(
            name = "locked_amount",
            nullable = false))
    private Money lockedAmount = new Money(0);

    @Embedded
    @AttributeOverride(name = "value", column = @Column(
            name = "total_spent",
            nullable = false))
    private Money totalSpent = new Money(0);

    @OneToOne(mappedBy = "wallet")
    private UserEntity user;

    @OneToMany(
            mappedBy = "wallet",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    public Wallet() {}

    public void deposit(Money amount){
        balance = balance.add(amount);
    }

    public void withdraw(Money amount){

        if (amount.isGreaterThan(balance)){
            throw new InsufficientBalanceException("Không đủ số dư");
        }

        balance = balance.subtract(amount);
    }

    public void lock(Money amount){ // Khóa khi bid

        if (amount.isGreaterThan(balance)) throw new InsufficientBalanceException("Không đủ số dư");

        balance = balance.subtract(amount);

        lockedAmount = lockedAmount.add(amount);
    }

    public void unlock(Money amount) { // Unlock khi outbid
        if (amount.isGreaterThan(lockedAmount)){
            throw new ValidationException("Có người ra giá lớn hơn");
        }
        lockedAmount = lockedAmount.subtract(amount);

        balance = balance.add(amount);
    }

    public void consumeLocked(Money amount){
        if (amount.isGreaterThan(lockedAmount)){
            throw new ValidationException("Số tiền không đủ");
        }

        lockedAmount = lockedAmount.subtract(amount);
    }

    public void addSpent(Money amount){

        totalSpent = totalSpent.add(amount);
    }
}