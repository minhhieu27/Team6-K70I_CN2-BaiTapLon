package com.app.exception.auction;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class BidConflictException extends AppException {
    
    public BidConflictException(String message){
        super(ErrorCode.BID_CONFLICT, message);
    }
}
