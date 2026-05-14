package com.app.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.response.UserResponse;
import com.app.service.UserService;


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
}
