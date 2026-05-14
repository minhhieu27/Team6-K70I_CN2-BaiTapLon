package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.app.dto.request.LoginRequest;
import com.app.dto.request.RegisterRequest;
import com.app.dto.response.LoginResponse;
import com.app.dto.response.UserResponse;
import com.app.entity.UserEntity;
import com.app.exception.security.LoginException;
import com.app.exception.user.AccountLockedException;
import com.app.repository.UserRepository;
import com.app.service.UserService;

import jakarta.validation.Valid;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register
    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest req){
        return userService.register(req.getUsername(), req.getEmail(), req.getPassword());
    }

    // Login
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) throws Exception{
        UserEntity userDb = userRepository.findByUsernameOrEmailOrPhone(req.getIdentifier(), req.getIdentifier(), req.getIdentifier())
                .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại"));

        if (!passwordEncoder.matches(req.getPassword(), userDb.getPassword())){
            throw new LoginException("Sai mật khẩu");
        }

        return userService.login(req.getIdentifier(), req.getPassword());
    }

    // Nâng cấp seller
    @PostMapping("/become-seller")
    public LoginResponse becomeSeller(Principal principal) throws AccountLockedException{

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
