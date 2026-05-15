package com.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.common.money.Money;
import com.app.dto.response.WalletResponse;
import com.app.entity.UserEntity;
import com.app.entity.Wallet;
import com.app.exception.security.UserNotFoundException;
import com.app.exception.wallet.InsufficientBalanceException;
import com.app.mapper.WalletMapper;
import com.app.repository.UserRepository;

@Service
public class WalletService {

    @Autowired 
    private UserRepository userRepository;

    @Autowired
    private WalletMapper walletMapper;

    // ====== GET WALLET ======
    public WalletResponse getWallet(String userId){

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        Wallet wallet = user.getWallet();

        return walletMapper.toResponse(wallet);
    }

    // ======= DEPOSIT ======
    public WalletResponse deposit(String userId, Money amount){

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        Wallet wallet = user.getWallet();

        wallet.deposit(amount);

        userRepository.save(user);

        return walletMapper.toResponse(wallet);
    }

    // ====== WITHDRAW ======
    public WalletResponse withdraw(String userId, Money amount){

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        Wallet wallet = user.getWallet();

        wallet.withdraw(amount);

        userRepository.save(user);

        return walletMapper.toResponse(wallet);
    }

    // ====== TRANSFER ======
    public void transfer(UserEntity from, UserEntity to, Money amount){

        Wallet sender = from.getWallet();

        Wallet receiver = to.getWallet();

        if (amount.isGreaterThan(sender.getBalance())){
            throw new InsufficientBalanceException("Không đủ tiền");
        }

        sender.withdraw(amount);
        receiver.deposit(amount);

        userRepository.save(from);
        userRepository.save(to);
    }

    // ====== REFUND ======
    public void refundBid(UserEntity user, Money amount){
        user.getWallet().unlock(amount);

        userRepository.save(user);
    }

    // ====== PAY SELLER ======
    public void paySeller(UserEntity winner, UserEntity seller, Money amount){

        winner.getWallet().consumeLocked(amount);

        seller.getWallet().deposit(amount);

        userRepository.save(winner);

        userRepository.save(seller);
    }
}
