package com.app.service.notification;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.app.dto.response.message.MessageResponse;
import com.app.dto.response.notification.NotificationResponse;
import com.app.entity.notification.NotificationEntity;
import com.app.entity.user.UserEntity;
import com.app.exception.notification.NotificationNotFoundException;
import com.app.exception.user.UserNotFoundException;
import com.app.exception.user.UserNotHasNotification;
import com.app.mapper.NotificationMapper;
import com.app.repository.NotificationRepository;
import com.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private final NotificationMapper notificationMapper;

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
    public Page<NotificationResponse> getUserNotifications(String userId, int page, int size){ // Lấy toàn bộ notification của 1 user
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("creataAt").descending());

        return notificationRepository.findByUser_UserIdOrderByCreateNotifyAtDesc(userId, pageable).map(notificationMapper::toResponse);
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
