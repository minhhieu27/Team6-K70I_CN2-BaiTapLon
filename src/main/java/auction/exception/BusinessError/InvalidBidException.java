package auction.exception.BusinessError;

import auction.exception.base.*;

public class InvalidBidException extends AppException { // Lỗi bid đấu giá
    public InvalidBidException(String message){
        super(message, ErrorCode.BID_VALID.name());
    }
}
