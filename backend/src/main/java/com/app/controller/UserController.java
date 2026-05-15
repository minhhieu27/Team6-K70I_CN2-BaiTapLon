package com.app.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.request.ChangePasswordRequest;
import com.app.dto.request.ResetPasswordRequest;
import com.app.dto.request.UpdateProfileRequest;
import com.app.dto.response.MessageResponse;
import com.app.dto.response.UserResponse;
import com.app.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    // ====== GET MY USERPROFILE ======
    @GetMapping("/me")
    public UserResponse me(Principal principal){
        
        return userService.getByUserId(principal.getName());
    }

    // ====== GET ALL USER ======
    @GetMapping
    public List<UserResponse> getAll(){

        return userService.getAll();
    }

    // ====== GET USER BY ID ======
    @GetMapping("/{userId}")
    public UserResponse getById(@PathVariable String userId){

        return userService.getByUserId(userId);
    }

    // ====== LOCK USER ======
    @PutMapping("/{userId}/lock")
    public String lockUser(@PathVariable String userId){

        userService.lockUser(userId);

        return "User đã bị khóa";
    }

    // ====== UNLOCK USER ======   
    @PutMapping("/{userId}/unlock")
    public String unlockUser(@PathVariable String userId){

        userService.unlockUser(userId);

        return "User đã được mở khóa";
    }

    // ====== CHANGE PASSWORD ======
    @PostMapping("/change-password")
    public MessageResponse changePassword(@Valid @RequestBody ChangePasswordRequest req, Principal principal){

        return userService.changePassword(principal.getName(), req);
    }

    // ====== RESET PASSWORD ======
    @PutMapping("/reset-password")
    public MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest req){

        return userService.resetPassword(req);
    }

    // ====== UPDATE PROFILE ======
    @PutMapping("/profile")
    public MessageResponse updateProfile(@Valid @RequestBody UpdateProfileRequest req, Principal principal){

        return userService.updateProfile(principal.getName(), req);
    }
}
