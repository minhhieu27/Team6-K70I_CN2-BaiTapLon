package com.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.wallet.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
 
    Page<Transaction> findByWallet_User_UserIdOrderByTimeDesc(String userId, Pageable pageable);
}
