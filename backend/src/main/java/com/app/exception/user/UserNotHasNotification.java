package com.app.exception.user;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class UserNotHasNotification extends AppException {
    public UserNotHasNotification(String message){
        super(ErrorCode.USER_NOT_HAVE_NOTIFICATION, message);
    }
}
