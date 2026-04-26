package auction.exception;

public class NetworkException extends AppException {
    
    public NetworkException(String message){

        super(message, ErrorCode.NETWORK_ERROR.name());
    }
}
