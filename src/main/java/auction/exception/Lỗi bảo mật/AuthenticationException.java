package auction.exception;

public class AuthenticationException extends AppException { // Lỗi đăng nhập
    public AuthenticationException(String message){
        super(message, ErrorCode.AUTH_ERROR.name());
    }
}
