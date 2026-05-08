package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.app.domain.enums.Role;
import com.app.entity.User;
import com.app.repository.UserRepository;
import com.app.security.JWTUtil;
import com.app.service.UserService;

import java.security.Principal;

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

        return JWTUtil.generateToken(userDb.getUsername(), userDb.getRoles());
    }

    // Nâng cấp seller
    @PostMapping("/become-seller")
    public String becomeSeller(Principal principal){
        String userId = principal.getName();
        User user = userRepository.findByUserId(userId).orElseThrow();

        user.getRoles().add(Role.ROLE_SELLER);
        userRepository.save(user);

        return JWTUtil.generateToken(user.getUserid(), user.getRoles());
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
