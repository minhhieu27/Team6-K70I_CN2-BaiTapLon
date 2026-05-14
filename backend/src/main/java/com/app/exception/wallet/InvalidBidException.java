package com.app.exception.wallet;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.*;

public class InvalidBidException extends AppException { // Lỗi bid đấu giá
    public InvalidBidException(String message){
        super(ErrorCode.INVALID_BID, message);
    }
}
