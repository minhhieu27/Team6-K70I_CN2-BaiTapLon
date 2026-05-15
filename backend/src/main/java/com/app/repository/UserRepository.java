package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameOrEmailOrPhone(String username, String email, String phone);

    Optional<UserEntity> findByUserId(String userId);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByPhoneOrEmail(String phone, String email);

    boolean existByEmail(String email);

    boolean existByUsername(String username);

    boolean existByPhone(String phone);
}
