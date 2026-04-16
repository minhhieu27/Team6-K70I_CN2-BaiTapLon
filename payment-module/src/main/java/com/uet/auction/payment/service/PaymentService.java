package com.uet.auction.payment.service;

import com.uet.auction.payment.model.Wallet;
import com.uet.auction.payment.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    @Autowired
    private WalletRepository walletRepository;

    @Transactional // <--- Quan trọng nhất: Đảm bảo tính "Hoặc tất cả hoặc không có gì"
    public String processPayment(String username, Double amount) {
        // Nhờ có @Lock ở Repository, dòng này sẽ đợi nếu có luồng khác đang xử lý ví này
        Wallet wallet = walletRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví của " + username));

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Số dư không đủ để thực hiện giao dịch!");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);

        return "Thanh toán thành công! Số dư còn lại: " + wallet.getBalance();
    }
}