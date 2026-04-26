package users.model.user;

public class UserProfile {
    private String name;
    private String phone;
    private String address;

    public UserProfile(String name, String phone, String address){
        this.name = name;
        setPhone(phone); // Dùng setter để validate
        this.address = address;
    }

    public UserProfile() {} // Tạo constructor rỗng dùng khi chưa có đầy đủ info

    public String getName(){
        return name;
    }

    public String getPhone(){
        return phone;
    }

    public String getAddress(){
        return address;
    }

    public void setPhone(String phone){
        if (phone != null && !phone.matches("0\\d{9}")){
            // Kiểm tra tính hợp lệ của SĐT
            // Phone.matches dùng để định dạng format SĐT
            // 0\\d{9} tức là SĐT bắt đầu bằng 0, lặp lại 9 lần các số từ (0-9)
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        }
        this.phone = phone;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setAddress(String address){
        this.address = address;
    }
}
