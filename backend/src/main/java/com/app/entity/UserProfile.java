package com.app.entity;

import com.app.exception.validation.ValidationException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserProfile {
    
    @Column(length = 10)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(length = 500)
    private String bio;

    protected UserProfile() {}

    public UserProfile(String phone){
        setPhone(phone);
    }

    public String getPhone(){
        return phone;
    }

    public String getAddress(){
        return address;
    }

    public String getBio(){
        return bio;
    }

    public void setPhone(String phone){
        if (phone == null || !phone.matches("0\\d{9}")){
            // Kiểm tra tính hợp lệ của SĐT
            // Phone.matches dùng để định dạng format SĐT
            // 0\\d{9} tức là SĐT bắt đầu bằng 0, lặp lại 9 lần các số từ (0-9)
            throw new ValidationException("Số điện thoại không hợp lệ");
        }
        this.phone = phone;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public void setBio(String bio){
        this.bio = bio;
    }
}
