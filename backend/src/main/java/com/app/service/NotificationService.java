package com.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entity.NotificationEntity;
import com.app.entity.UserEntity;
import com.app.exception.user.NotificationNotFoundException;
import com.app.repository.NotificationRepository;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository){

        this.notificationRepository = notificationRepository;
    }

    // ====== CREATE ======
    public void notifyUser(UserEntity user, String message){

        NotificationEntity notification = new NotificationEntity(message, user);

        notification.setUser(user);
        notification.setMessage(message);
        
        notificationRepository.save(notification);
    }

    // ====== GET USER NOTIFICATIONS ======
    public List<NotificationEntity> getUserNotifications(String userId){ // Lấy toàn bộ notification của 1 user
        return notificationRepository.findByUser_UserIdOrderByCreateAtDesc(userId);
    }

    // ====== MARK AS READ ======
    public void markAsRead(String notificationId){ // Đánh dấu thông báo đã đọc

        NotificationEntity notification = notificationRepository.findById(notificationId).orElseThrow(() -> new NotificationNotFoundException("Không tìm thấy thông báo"));

        notification.setRead(true);

        notificationRepository.save(notification);
    }
}
