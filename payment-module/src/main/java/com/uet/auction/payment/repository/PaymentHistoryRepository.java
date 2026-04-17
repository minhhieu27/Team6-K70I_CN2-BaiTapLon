package com.uet.auction.payment.repository;

import com.uet.auction.payment.model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findByUsername(String username); // Tìm lịch sử theo tên bác
}