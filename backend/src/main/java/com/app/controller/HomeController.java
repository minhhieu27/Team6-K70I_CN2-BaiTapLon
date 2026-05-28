package com.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.response.message.MessageResponse;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public MessageResponse home(){

        return new MessageResponse("Auction API is running");
    }
}
