package com.app.exception.base;

import com.app.common.enums.ErrorCode;

public abstract class AppException extends RuntimeException {
    
    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }
}
