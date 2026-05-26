package com.app.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.request.security.ChangePasswordRequest;
import com.app.dto.request.security.ResetPasswordRequest;
import com.app.dto.request.user.UpdateProfileRequest;
import com.app.dto.response.message.MessageResponse;
import com.app.dto.response.user.UserResponse;
import com.app.service.user.UserService;

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
    @PutMapping("/me/lock")
    public MessageResponse lockUser(Authentication authentication){

        userService.lockUser(authentication.getName());

        return new MessageResponse( "User đã bị khóa");
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
