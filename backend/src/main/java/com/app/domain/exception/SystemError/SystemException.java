package com.app.domain.exception.SystemError;

import com.app.domain.exception.base.*;

public class SystemException extends AppException {
    
    public SystemException(String message){

        super(message, ErrorCode.SYSTEM_ERROR.name());
    }
}
