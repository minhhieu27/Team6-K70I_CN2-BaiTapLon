package auction.exception;

public class InvalidBidException extends Exception { // Lỗi bid đấu giá
    public InvalidBidException(String msg){
        super(msg);
    }
}
