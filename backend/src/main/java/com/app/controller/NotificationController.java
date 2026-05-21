package com.app.controller;

import com.app.service.NotificationService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.response.MessageResponse;
import com.app.entity.NotificationEntity;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;

    NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ====== MY NOTIFICATIONS ======
    @GetMapping
    public List<NotificationEntity> myNotifications(Authentication authentication) {

        return notificationService.getUserNotifications(authentication.getName());
    }

    // ====== MARK AS READ ======
    @PutMapping("/{notificationId}/read")
    public MessageResponse markAsRead(@PathVariable String notificationId, Authentication authentication){

        return notificationService.markAsRead(authentication.getName(), notificationId);
    }

    // ====== READ ALL ======
    @PutMapping("/read-all")
    public MessageResponse readAll(Authentication authentication){

        return notificationService.readAll(authentication.getName());
    }
}
