package auction.exception;

public class AuthenticationException extends Exception { // Lỗi đăng nhập
    public AuthenticationException(String msg){
        super(msg);
    }
}
