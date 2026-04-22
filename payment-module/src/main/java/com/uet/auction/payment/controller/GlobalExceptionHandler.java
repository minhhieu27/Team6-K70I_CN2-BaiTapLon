package com.uet.auction.payment.controller;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    // Bắt lỗi tranh chấp dữ liệu (Race Condition)
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class) // xử lý lỗi 
    public ResponseEntity<String> handleConflict(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body("Hệ thống đang bận do có giao dịch cùng lúc. Vui lòng thử lại sau giây lát!");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeError(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Lỗi hệ thống: " + ex.getMessage());
    }
}