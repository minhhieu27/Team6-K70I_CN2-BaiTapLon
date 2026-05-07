package com.app.domain.exception.ConcurrentError;

import com.app.domain.exception.base.*;

public class ConcurrencyException extends AppException {

    public ConcurrencyException(String message){
        super(message, ErrorCode.CONCURRENCE_ERROR.name());
    }
}
