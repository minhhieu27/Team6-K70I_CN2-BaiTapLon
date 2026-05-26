package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.user.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameOrUserProfile_EmailOrUserProfile_Phone(String username, String userProfile_Email, String userProfile_Phone);

    Optional<UserEntity> findByUserId(String userId);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByUserProfile_PhoneOrUserProfile_Email(String userProfile_phone, String userProfile_email);

    boolean existsByUserProfile_Email(String userProfile_email);

    boolean existsByUsername(String username);

    boolean existsByUserProfile_Phone(String userProfile_phone);
}
