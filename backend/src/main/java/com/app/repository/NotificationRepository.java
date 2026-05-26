package com.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.notification.NotificationEntity;

public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {
    Page<NotificationEntity> findByUser_UserIdOrderByCreateAtDesc(String userId, Pageable pageable);

    List<NotificationEntity> findByUser_UserId(String userId);
}
