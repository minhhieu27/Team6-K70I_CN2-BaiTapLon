package com.app.entity;

import java.time.LocalDateTime;

import com.app.common.enums.TransactionType;
import com.app.common.money.Money;

import jakarta.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(
                                                name = "amount",
                                                nullable = false))
    private Money amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    protected Transaction() {}

    public Transaction(Money amount, TransactionType type, Wallet wallet){
        this.amount = amount;
        this.type = type;
        this.wallet = wallet;
        this.time = LocalDateTime.now();
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

    public Wallet getWallet(){
        return wallet;
    }
}
