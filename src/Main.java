import service.UserService;
import model.User;

public class Main {
    public static void main(String[] args) {

        UserService us = new UserService();

        // đăng ký
        us.register("admin", "123", "ADMIN");
        us.register("user1", "123", "USER");

        // đăng nhập đúng
        us.login("user1", "123");

        // nhập sai
        us.login("user1", "111");
        us.login("user1", "111");
        us.login("user1", "111");

        // thử lại sau khi bị khóa
        us.login("user1", "123");

        // admin mở khóa
        us.unlockUser("user1");

        // đăng nhập lại
        us.login("user1", "123");

        // xem danh sách
        us.showUsers();
    }
}
