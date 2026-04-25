package user.model;

import user.enums.UserStatus;

public class UserAccount {
    private String email;
    private String password;
    private UserStatus status;

    public UserAccount(String email, String password){
        this.email = email;
        this.password = password;
        this.status = UserStatus.ACTIVE;
    }

    public boolean isActive(){
        return status == UserStatus.ACTIVE;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    public void setStatus(UserStatus status){
        this.status = status;
    }
}
