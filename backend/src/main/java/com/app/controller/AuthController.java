package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.app.entity.User;
import com.app.repository.UserRepository;
import com.app.security.JWTUtil;
import com.app.service.UserService;

import java.security.Principal;
import java.util.stream.Collectors;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class AuthController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register
    @PostMapping("/register")
    public User register(@RequestBody User user){
        return userService.register(user);
    }

    // Login
    @PostMapping("/login")
    public String login(@RequestBody User user){
        User userDb = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        if (!passwordEncoder.matches(user.getPassword(), userDb.getPassword())){
            throw new RuntimeException("Sai mật khẩu");
        }

        Set<String> roles = userDb.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return JWTUtil.generateToken(userDb.getUsername(), roles);
    }

    // Nâng cấp seller
    @PostMapping("/become-seller")
    public String becomeSeller(Principal principal){
        userService.becomeSeller(principal.getName());
        return "Bạn đã trở thành SELLER";
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
