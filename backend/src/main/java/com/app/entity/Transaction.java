package com.app.entity;

import java.time.LocalDateTime;

import com.app.common.enums.TransactionType;
import com.app.common.money.Money;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
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
    @Column(nullable = false)
    private TransactionType type;

    @Column(name = "transaction_time")
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    protected Transaction() {}

    public Transaction(Money amount, TransactionType type, Wallet wallet){
        this.amount = amount;
        this.type = type;
        this.wallet = wallet;
        this.time = LocalDateTime.now();
    }
}
