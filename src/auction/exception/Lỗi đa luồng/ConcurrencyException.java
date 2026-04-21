package auction.exception;

public class ConcurrencyException {

    public ConcurrencyException(String message){
        super(message, ErrorCode.CONCURRENCE_ERROR.name());
    }
}
