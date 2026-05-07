package com.app.domain.exception.SecurrityError;

import com.app.domain.exception.base.*;

public class AuthorizationException extends AppException {
    
    public AuthorizationException(String message){
        super(message, ErrorCode.ACCESS_DINED.name());
    }
}
