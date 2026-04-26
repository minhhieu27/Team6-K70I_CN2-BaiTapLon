package auction.exception;

public class Validation extends AppException {
    
    public Validation(String message){
        super(message, ErrorCode.VALIDATION_ERROR.name());
    }
}
