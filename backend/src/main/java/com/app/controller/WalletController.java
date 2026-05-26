package com.app.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.common.money.Money;
import com.app.dto.request.wallet.DepositRequest;
import com.app.dto.request.wallet.WithdrawRequest;
import com.app.dto.response.wallet.TransactionResponse;
import com.app.dto.response.wallet.WalletResponse;
import com.app.entity.user.UserEntity;
import com.app.exception.user.UserNotFoundException;
import com.app.repository.UserRepository;
import com.app.service.wallet.TransactionService;
import com.app.service.wallet.WalletService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    
    @Autowired
    private WalletService walletService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    // ====== GET WALLET ======
    @GetMapping
    public WalletResponse getWallet(Principal principal){
        return walletService.getWallet(principal.getName());
    }

    // ====== DEPOSIT ======
    @PostMapping("/deposit")
    public WalletResponse deposit(@Valid @RequestBody DepositRequest req, Principal principal){
        
        return walletService.deposit(principal.getName(), new Money(req.getAmount()));
    }

    // ====== WITHDRAW ======
    @PostMapping("/withdraw")
    public WalletResponse withdraw(@Valid @RequestBody WithdrawRequest req, Principal principal){

        return walletService.withdraw(principal.getName(), new Money(req.getAmount()));
    }

    // ====== TRANSACTION ======
    @GetMapping("/transactions")
    public Page<TransactionResponse> myTransactions(Authentication authentication,
                                                    @RequestParam (defaultValue = "0") int page,
                                                    @RequestParam (defaultValue = "10") int size){
        
        UserEntity user = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        return transactionService.getUserTransactions(user.getUserId(), page, size);
    }
}
