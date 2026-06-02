package com.app.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {
    
    // Thêm :"" (hoặc giá trị mặc định) để nếu không tìm thấy biến môi trường thì nó dùng rỗng
    @Value("${cloudinary.cloud-name:dummy}")
    private String cloudName;

    @Value("${cloudinary.api-key:dummy}")
    private String apiKey;

    @Value("${cloudinary.api-secret:dummy}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary(){
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dummydummy"); 
        config.put("api_key", "123456789012345");
        config.put("api_secret", "dummydummydummydummydummy");
        return new Cloudinary(config);
    }
}