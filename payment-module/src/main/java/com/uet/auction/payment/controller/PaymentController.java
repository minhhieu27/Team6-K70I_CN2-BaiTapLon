package com.uet.auction.payment.controller;

import com.uet.auction.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public String pay(@RequestParam String username, @RequestParam Double amount) {
        return paymentService.processPayment(username, amount);
    }
}