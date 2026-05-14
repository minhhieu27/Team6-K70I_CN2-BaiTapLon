package com.app.service;

import org.springframework.stereotype.Service;

import com.app.entity.UserEntity;
import com.app.entity.UserProfile;

@Service
public class UserProfileService {
    
    public UserProfile getProfile(UserEntity user){

        return user.getProfile();
    }
}
