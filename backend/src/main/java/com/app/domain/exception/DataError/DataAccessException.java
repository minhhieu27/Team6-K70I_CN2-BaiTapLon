package com.app.domain.exception.DataError;

import com.app.domain.exception.base.*;

public class DataAccessException extends AppException {

    public DataAccessException(String message){
        super(message, ErrorCode.DATA_ERROR.name());
    }
}
