package com.app.exception.item;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class InvalidItemTypeException extends AppException {
    
    public InvalidItemTypeException(String message){
        super(ErrorCode.INVALID_ITEM_TYPE, message);
    }
}
