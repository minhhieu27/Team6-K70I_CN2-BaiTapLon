package com.app.domain.service;

import com.app.domain.user.*;

public class UserProfileService {
    
    public UserProfile getProfile(User user){

        return new  UserProfile(
            user.getId(),
            user.getProfile().getPhone(),
            user.getVIPLevel().name(),
            user.getAccount().getStatus().name()
        );
    }
}
