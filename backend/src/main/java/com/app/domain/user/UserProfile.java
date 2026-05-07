package com.app.domain.user;

public class UserProfile {
    private String id;
    private String phone;
    private String vipLevel;
    private String status;

    public UserProfile(String id, String vipLevel, String status, String phone){
        this.id = id;
        setPhone(phone); // Dùng setter để validate
        this.vipLevel = vipLevel;
        this.status = status;
    }

    public UserProfile() {} // Tạo constructor rỗng dùng khi chưa có đầy đủ info

    public String getId(){
        return id;
    }

    public String getPhone(){
        return phone;
    }

    public String getVipLevel(){
        return vipLevel;
    }

    public String getStatus(){
        return status;
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
}
