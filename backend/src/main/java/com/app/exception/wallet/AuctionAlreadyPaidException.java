package com.app.exception.wallet;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class AuctionAlreadyPaidException extends AppException {
    public AuctionAlreadyPaidException(String message){
        super(ErrorCode.AUCTION_ALREADY_PAID, message);
    }
}
