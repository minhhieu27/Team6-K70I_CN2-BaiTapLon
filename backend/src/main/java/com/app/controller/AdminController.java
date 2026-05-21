package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.response.MessageResponse;
import com.app.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    @DeleteMapping("/auction/{auctionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public MessageResponse deleteAuction(@PathVariable String auctionId){

        return adminService.deleteAuction(auctionId);
    }

    @PostMapping("/ban/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public MessageResponse banUser(@PathVariable String userId){

        return adminService.banUser(userId);
    }
}
