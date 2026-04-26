package auction.exception.SecurrityError;

import auction.exception.base.*;

public class AuthenticationException extends AppException { // Lỗi đăng nhập
    public AuthenticationException(String message){
        super(message, ErrorCode.AUTH_ERROR.name());
    }
}
