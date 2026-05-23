package com.app.entity;

import com.app.exception.validation.ValidationException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table (name = "users_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String fullname;

    @Column(length = 255)
    private String address;

    @Column(length = 500)
    private String bio;

    @Column(length = 500)
    private String avatar;

    @Column (name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column (name = "phone", length = 20, unique = true)
    private String phone;

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

    public String getEmail(){
        return email;
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

    public void setAvatar(String avatar){
        this.avatar = avatar;
    }

    public void setFullname(String fullname){
        this.fullname = fullname;
    }

    public void setEmail(String email){
        this.email = email;
    }
}
