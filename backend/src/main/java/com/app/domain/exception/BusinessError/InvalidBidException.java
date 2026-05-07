package com.app.domain.exception.BusinessError;

import com.app.domain.exception.base.*;

public class InvalidBidException extends AppException { // Lỗi bid đấu giá
    public InvalidBidException(String message){
        super(message, ErrorCode.BID_VALID.name());
    }
}
