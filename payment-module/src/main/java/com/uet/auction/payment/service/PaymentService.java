package com.uet.auction.payment.service;

import com.uet.auction.payment.model.PaymentHistory;
import com.uet.auction.payment.model.Wallet;
import com.uet.auction.payment.repository.PaymentHistoryRepository;
import com.uet.auction.payment.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private PaymentHistoryRepository historyRepository;

    @Transactional
    public String processPayment(String username, Double amount) {
        // 1. Chặn rút tiền âm/bằng 0 bằng if-else
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Số tiền thanh toán phải lớn hơn 0!");
        }

        PaymentHistory history = new PaymentHistory();
        history.setUsername(username);
        history.setAmount(amount);
        history.setDescription("Thanh toán đấu giá");

        try {
            Wallet wallet = walletRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ví của " + username));

            if (wallet.getBalance() < amount) {
                throw new RuntimeException("Số dư không đủ!");
            }

            wallet.setBalance(wallet.getBalance() - amount);
            walletRepository.save(wallet);

            history.setStatus("SUCCESS");
            historyRepository.save(history);

            return "Thanh toán thành công! Số dư còn lại: " + wallet.getBalance();

        } catch (Exception e) {
            history.setStatus("FAILED");
            history.setDescription("Lỗi: " + e.getMessage());
            historyRepository.save(history);
            throw e; 
        }
    }
}