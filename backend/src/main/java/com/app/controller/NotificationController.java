package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.response.message.MessageResponse;
import com.app.dto.response.notification.NotificationResponse;
import com.app.service.notification.NotificationService;

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
    public Page<NotificationResponse> myNotifications(Authentication authentication, 
                                                    @RequestParam (defaultValue = "0") int page,
                                                    @RequestParam (defaultValue = "10") int size) {

        return notificationService.getUserNotifications(authentication.getName(),page, size);
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
