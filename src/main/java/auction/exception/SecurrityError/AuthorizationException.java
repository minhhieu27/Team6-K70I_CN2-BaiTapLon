package auction.exception;

public class AuthorizationException extends AppException {
    
    public AuthorizationException(String message){
        super(message, ErrorCode.ACCESS_DINED.name());
    }
}
