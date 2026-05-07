package com.app.domain.exception.NetworkError;

import com.app.domain.exception.base.*;

public class NetworkException extends AppException {
    
    public NetworkException(String message){

        super(message, ErrorCode.NETWORK_ERROR.name());
    }
}
