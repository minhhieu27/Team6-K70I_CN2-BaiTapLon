package com.uet.auction.payment.repository;

import com.uet.auction.payment.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUsername(String username);
}