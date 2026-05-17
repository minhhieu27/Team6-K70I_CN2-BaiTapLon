package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.entity.NotificationEntity;
import com.app.repository.NotificationRepository;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping
    public List<NotificationEntity> getAll(){

        return notificationRepository.findAll();
    }
}
