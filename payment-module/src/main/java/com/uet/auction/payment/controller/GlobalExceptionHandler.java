package com.uet.auction.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Lưới này sẽ bắt mọi lỗi RuntimeException bác ném ra ở Service
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        // Trả về nội dung lỗi bác viết + Mã lỗi 400 (Bad Request) thay vì 500
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi hệ thống: " + ex.getMessage());
    }
}