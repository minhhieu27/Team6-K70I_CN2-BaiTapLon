package com.app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "notifications")
public class NotificationEntity {
    
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private String notificationId;

    private String message;

    private boolean isRead = false;

    private LocalDateTime createAt = LocalDateTime.now();

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "user_id")
    private UserEntity user;

    public NotificationEntity(String message, UserEntity user){

        this.message = message;
        this.user = user;
    }
}
