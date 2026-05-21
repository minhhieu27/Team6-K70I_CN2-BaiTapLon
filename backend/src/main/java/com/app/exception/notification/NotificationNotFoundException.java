package com.app.exception.notification;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class NotificationNotFoundException extends AppException{
    
    public NotificationNotFoundException(String message){
        super(ErrorCode.NOTIFICATION_NOT_FOUND, message);
    }
}
