package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.dto.request.security.LoginRequest;
import com.app.dto.request.security.RegisterRequest;
import com.app.dto.response.security.LoginResponse;
import com.app.dto.response.user.UserResponse;
import com.app.service.user.UserService;

import jakarta.validation.Valid;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;

    // Register
    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest req){

        return userService.register(req.getUsername(), req.getEmail(), req.getPhone(), req.getPassword());
    }

    // Login
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) throws Exception{

        return userService.login(req.getIdentifier(), req.getPassword());
    }

    // Nâng cấp seller
    @PostMapping("/become-seller")
    public LoginResponse becomeSeller(Principal principal){

        return userService.becomeSeller(principal.getName());
    }

    // Test seller
    @GetMapping("/seller/create-auction")
    public String createAuction(){
        return "Tạo Auction thành công!";
    }

    // Test user
    @GetMapping("/hello")
    public String hello(){
        return "Hello user!";
    }

}
