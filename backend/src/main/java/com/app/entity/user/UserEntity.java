package com.app.entity.user;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.app.common.enums.Role;
import com.app.common.enums.UserStatus;
import com.app.common.enums.VIPLevel;
import com.app.common.money.Money;
import com.app.common.tool.IDGenerator;
import com.app.entity.wallet.Wallet;
import com.app.exception.user.AccountBannedException;
import com.app.exception.user.AccountLockedException;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table (name = "users")
public class UserEntity {
    
    @Id
    @GeneratedValue (strategy =  GenerationType.IDENTITY)
    private long id;

    @Column (name = "user_name", nullable =  false, length = 50, unique = true)
    private String username;

    @Column (name = "user_id", nullable = false, length = 20, unique = true)
    private String userId;

    @Column (name = "password", nullable = false, length = 255)
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private UserProfile userProfile = new UserProfile();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "walleta_id", referencedColumnName = "id")
    private Wallet wallet;

    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserRoleEntity> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VIPLevel vipLevel = VIPLevel.NORMAL;

    @Column(name = "create_at")
    private LocalDateTime create_User_At;

    @Column(name = "update_at")
    private LocalDateTime update_At;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    public UserEntity() {
    }

    public UserEntity(String username, String email, String phone, String password){
        this.userId = IDGenerator.generateUserId();
        this.username = username;
        this.userProfile.setEmail(email);
        this.userProfile.setPhone(phone);
        this.password = password;

        this.wallet = new Wallet();

        UserRoleEntity roleEntity = new UserRoleEntity();

        roleEntity.setRole(Role.ROLE_USER);
        roleEntity.setUser(this);

        this.roles.add(roleEntity);
    }

    public UserRoleEntity createRoleEntity(Role role){

        UserRoleEntity roleEntity = new UserRoleEntity();

        roleEntity.setRole(role);
        roleEntity.setUser(this);

        return roleEntity;
    }

    @PrePersist
    public void onCreate() {
        this.create_User_At = LocalDateTime.now();
        this.update_At = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate(){
        this.update_At = LocalDateTime.now();
    }

    // ====== ROLE ======
    public void addRole(Role role){
        roles.add(createRoleEntity(role));
    }

    public void removeRole(Role role){
        roles.removeIf(r -> r.getRole() == role);
    }

    public boolean hasRole(Role role){
        return roles.stream().anyMatch(r ->r.getRole() == role);
    }

    public boolean hasAnyRole(Role... roles){
        
        return Arrays.stream(roles).anyMatch(this::hasRole);
    }

    public void becomeSeller() {
        addRole(Role.ROLE_SELLER);
    }

    public boolean isSeller(){
        return hasRole(Role.ROLE_SELLER);
    }

    // ====== VIP ======

    public void upgradeVIP(){
        Money total = wallet.getTotalSpent();

        VIPLevel newLevel = VIPLevel.NORMAL;

        for (VIPLevel level : VIPLevel.values()){
            if (total.isGreaterThan(level.getRequiredWithdraw()) || total.isEqual(level.getRequiredWithdraw())){
                newLevel = level;
            }
        }

        this.vipLevel = newLevel;
    }

    // ====== STATUS ======
    public void lockAccount(){
        this.status = UserStatus.LOCKED;
    }

    public void unlockAccount(){
        this.status = UserStatus.ACTIVE;
    }

    public void banAccount(){
        this.status = UserStatus.BANNED;
    }

    public void validateActive(){
        if (status == UserStatus.LOCKED){
            throw new AccountLockedException("Tài khoản bị khóa");
        }

        if (status == UserStatus.BANNED){
            throw new AccountBannedException("Tài khoản bị cấm");
        }
    }
}
