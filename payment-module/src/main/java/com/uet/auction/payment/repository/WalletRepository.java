package com.uet.auction.payment.repository;

import com.uet.auction.payment.model.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    // Thêm @Lock để khi tìm ví, MySQL sẽ khóa dòng đó lại, không cho luồng khác sửa
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findByUsername(String username);
}