package com.app.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.service.image.ImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
    
    private final ImageService imageService;

    @PostMapping(value = "/upload",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestParam MultipartFile file){

        return imageService.upload(file);
    }
}
