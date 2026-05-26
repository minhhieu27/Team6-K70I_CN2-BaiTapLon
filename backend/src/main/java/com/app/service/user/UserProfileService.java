package com.app.service.user;

import org.springframework.stereotype.Service;

import com.app.entity.user.UserEntity;
import com.app.entity.user.UserProfile;

@Service
public class UserProfileService {
    
    public UserProfile getProfile(UserEntity user){

        return user.getUserProfile();
    }
}
