package com.app.exception;

import java.nio.file.AccessDeniedException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.app.exception.auction.BidConflictException;
import com.app.exception.base.AppException;;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ====== APP EXCEPTION ======
    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> handleAppException(AppException e){

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "error", e.getErrorCode(),
                        "message", e.getMessage()));
    }
    
    // ====== LOGIN FAIL ======
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException e){

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", "SYSTEM_ERROR",
                        "message", "Sai tài khoản hoặc mật khẩu"
                ));
    }

    // ====== FORBIDDEN ======
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAcessDenied(AccessDeniedException e){

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "error", "ACCESS_DENIED",
                        "message", "Không có quyền truy cập"
                ));
    }

    // ====== RUNTIME ======
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException e){

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "error", "RUNTIME_ERROR",
                        "message", e.getMessage()  
                ));
    }

    // ====== SYSTEM ======
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e){

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "SYSTEM_ERROR",
                        "message", e.getMessage()  
                ));
    }

    // ====== BID CONFLICT ======
    @ExceptionHandler(BidConflictException.class)
    public ResponseEntity<?> handleBidConflict(BidConflictException e){

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", "BID_CONFLICT",
                        "message", e.getMessage()
                ));
    }
}
