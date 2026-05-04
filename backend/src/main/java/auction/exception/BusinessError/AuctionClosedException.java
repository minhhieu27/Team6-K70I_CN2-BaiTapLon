package auction.exception.BusinessError;

import auction.exception.base.*;

public class AuctionClosedException extends AppException { // Lỗi kết thúc phiên đấu giá
    public AuctionClosedException(String message){
        super(message, ErrorCode.AUCTION_CLOSED.name());
    }
}
