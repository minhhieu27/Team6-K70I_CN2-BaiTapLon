package com.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.app.common.enums.Role;
import com.app.common.enums.UserStatus;
import com.app.entity.user.UserEntity;
import com.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppStartRunner implements CommandLineRunner {
    
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String...args){

        boolean adminExists = userRepository.existsByUsername("admin");

        if (adminExists){
            return;
        }

        UserEntity admin = new UserEntity("admin", "admin@app.com", "0000000000", passwordEncoder.encode("admin123"));

        admin.setStatus(UserStatus.ACTIVE);

        admin.addRole(Role.ROLE_ADMIN);

        userRepository.save(admin);

        log.info("Default admin created");

    }
}
