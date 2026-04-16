package com.uet.auction.payment.controller;

import com.uet.auction.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Đường dẫn để test: http://localhost:8080/api/payment/pay?username=ducanh&amount=100
    @PostMapping("/pay")
    public String pay(@RequestParam String username, @RequestParam Double amount) {
        return paymentService.processPayment(username, amount);
    }
}