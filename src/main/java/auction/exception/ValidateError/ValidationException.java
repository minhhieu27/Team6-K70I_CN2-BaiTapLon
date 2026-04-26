package auction.exception.ValidateError;

import auction.exception.base.*;

public class ValidationException extends AppException {
    
    public ValidationException(String message){
        super(message, ErrorCode.VALIDATION_ERROR.name());
    }
}
