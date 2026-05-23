package com.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dto.response.MessageResponse;
import com.app.entity.NotificationEntity;
import com.app.entity.UserEntity;
import com.app.exception.notification.NotificationNotFoundException;
import com.app.exception.user.UserNotFoundException;
import com.app.exception.user.UserNotHasNotification;
import com.app.repository.NotificationRepository;
import com.app.repository.UserRepository;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

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

    // ====== NOTIFY ALL USERS ======
    public void notifyAllUsers(String message){

        List<UserEntity> users = userRepository.findAll();

        for (UserEntity user : users){

            NotificationEntity notification = new NotificationEntity(message, user);

            notificationRepository.save(notification);
        }
    }

    // ====== GET USER NOTIFICATIONS ======
    public List<NotificationEntity> getUserNotifications(String userId){ // Lấy toàn bộ notification của 1 user
        return notificationRepository.findByUser_UserIdOrderByCreateAtDesc(userId);
    }

    // ====== MARK AS READ ======
    public MessageResponse markAsRead(String userId, String notificationId){ // Đánh dấu thông báo đã đọc

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        NotificationEntity notification = notificationRepository.findById(notificationId).orElseThrow(() -> new NotificationNotFoundException("Không tìm thấy thông báo"));

        if (!notification.getUser().getUserId().equals(user.getUserId())){
            throw new UserNotHasNotification("Không có quyền");
        }

        notification.setRead(true);

        notificationRepository.save(notification);

        return new MessageResponse("Đã đọc thông báo");
    }

    // ====== READ ALL ======
    public MessageResponse readAll(String userId){

        List<NotificationEntity> notifications = notificationRepository.findByUser_UserId(userId);

        for (NotificationEntity notification : notifications){

            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);

        return new MessageResponse("Đã đọc toàn bộ thông báo");
    }
}
