package model;

public class User {
    public int loginAttempts = 0;
    public String username;
    public String password;
    public String role; // "ADMIN" hoặc "USER"
    public boolean isLocked = false;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
