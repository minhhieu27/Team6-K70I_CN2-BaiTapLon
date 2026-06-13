package com.uet.auction.payment.controller;

import com.uet.auction.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController // chuyển kiểu dữ liệu trả về thành dạng json 
@RequestMapping("/api/payment")
public class PaymentController {
//    @Autowired // tự động khởi tạo đối tượng PaymentService và gán vào biến này
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public String pay(@RequestParam String username, @RequestParam Double amount) {
        return paymentService.processPayment(username, amount);
    }
}