package service;

import model.User;
import java.util.ArrayList;

public class UserService {

    public ArrayList<User> users = new ArrayList<>();

    // ================= ĐĂNG KÝ =================
    public void register(String username, String password, String role) {

        // kiểm tra trùng username
        for (User u : users) {
            if (u.username.equals(username)) {
                System.out.println("Username đã tồn tại!");
                return;
            }
        }

        // mã hóa password (fake)
        String encrypted = password + "_123";

        users.add(new User(username, encrypted, role));
        System.out.println("Đăng ký thành công: " + username);
    }

    // ================= ĐĂNG NHẬP =================
    public User login(String username, String password) {

        for (User u : users) {

            if (u.username.equals(username)) {

                // kiểm tra bị khóa
                if (u.isLocked) {
                    System.out.println("Tài khoản bị khóa!");
                    return null;
                }

                // kiểm tra mật khẩu
                if (u.password.equals(password + "_123")) {
                    u.loginAttempts = 0;
                    System.out.println("Đăng nhập thành công!");
                    return u;
                } else {
                    u.loginAttempts++;

                    if (u.loginAttempts >= 3) {
                        u.isLocked = true;
                        System.out.println("Bị khóa do nhập sai nhiều lần!");
                    } else {
                        System.out.println("Sai mật khẩu!");
                    }
                    return null;
                }
            }
        }

        System.out.println("Không tìm thấy user!");
        return null;
    }

    // ================= KHÓA USER =================
    public void lockUser(String username) {

        for (User u : users) {
            if (u.username.equals(username)) {
                u.isLocked = true;
                System.out.println("Đã khóa: " + username);
                return;
            }
        }

        System.out.println("Không tìm thấy user để khóa!");
    }

    // ================= MỞ KHÓA =================
    public void unlockUser(String username) {

        for (User u : users) {
            if (u.username.equals(username)) {
                u.isLocked = false;
                u.loginAttempts = 0;
                System.out.println("Đã mở khóa: " + username);
                return;
            }
        }

        System.out.println("Không tìm thấy user!");
    }

    // ================= HIỂN THỊ USER =================
    public void showUsers() {
        for (User u : users) {
            System.out.println(
                    "Username: " + u.username +
                            " | Role: " + u.role +
                            " | Locked: " + u.isLocked
            );
        }
    }
}