package com.app.exception.security;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.*;

public class AuthorizationException extends AppException {
    
    public AuthorizationException(String message){
        super(ErrorCode.ACCESS_DENIED, message);
    }
}
