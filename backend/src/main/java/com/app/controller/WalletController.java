package com.app.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.common.money.Money;
import com.app.dto.request.DepositRequest;
import com.app.dto.request.WithdrawRequest;
import com.app.dto.response.WalletResponse;
import com.app.service.WalletService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    
    @Autowired
    private WalletService walletService;

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
}
