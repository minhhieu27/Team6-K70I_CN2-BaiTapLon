package com.uet.auction.payment.service;

import com.uet.auction.payment.model.Wallet;
import com.uet.auction.payment.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    
    @Autowired
    private WalletRepository walletRepository;

    public String processPayment(String username, Double amount) {
        // 1. Tìm ví của bác dựa trên tên người dùng
        Wallet wallet = walletRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy ví của bác " + username));

        // 2. Kiểm tra xem trong ví còn đủ tiền để đấu giá không
        if (wallet.getBalance() < amount) {
            return "Thanh toán thất bại: Bác không đủ tiền rồi, nạp thêm đi!";
        }

        // 3. Thực hiện trừ tiền và lưu vào Database
        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);
        
        return "Thanh toán thành công! Số dư còn lại: " + wallet.getBalance();
    }
}