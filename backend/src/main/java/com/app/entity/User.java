package com.app.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.app.domain.enums.Role;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table (name = "users")
@Data
public class User {
    
    @Id
    @GeneratedValue (strategy =  GenerationType.IDENTITY)
    private long id;

    @Column (name = "user_name", nullable =  false, length = 50, unique = true)
    private String username;

    @Column (name = "user_id", nullable = false,length = 20, unique = true)
    private String userid;

    @Column (name = "email", length = 100, unique = true)
    private String email;

    @Column (name = "password",nullable = true, length = 255)
    private String password;

    @Column (precision = 15, scale = 2)
    private BigDecimal balance;

    @Column (name = "created_at")
    private LocalDateTime createAt;

    @Column (name = "update_at")
    private LocalDateTime updateAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    public void onCreate() {
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate(){
        this.updateAt = LocalDateTime.now();
    }
}
