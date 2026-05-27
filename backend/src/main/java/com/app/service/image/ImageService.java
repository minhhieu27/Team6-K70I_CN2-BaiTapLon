package com.app.service.image;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {
    
    private final Cloudinary cloudinary;

    public String upload(MultipartFile file){

        try {

            Map<?, ?> uploadResult = cloudinary.uploader()
                                        .upload(file.getBytes(), ObjectUtils.emptyMap());
            
            return uploadResult.get("secure_url").toString();
        
        }catch (IOException e){

            throw new RuntimeException("Upload image failed");
        }
    }
}
