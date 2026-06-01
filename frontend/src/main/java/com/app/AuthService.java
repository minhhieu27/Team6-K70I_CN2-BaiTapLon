package com.app;

import java.net.URI;
import java.net.http.*;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    private final HttpClient client = HttpClient.newHttpClient();
    
    // Gọi API Đăng nhập
    public CompletableFuture<HttpResponse<String>> login(String identifier, String password) {
        String json = String.format("{\"identifier\":\"%s\", \"password\":\"%s\"}", identifier, password);
        
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
    }

    // Gọi API Đăng ký
    public CompletableFuture<HttpResponse<String>> register(String username, String email, String phone, String password) {
        String json = String.format("{\"username\":\"%s\", \"email\":\"%s\", \"phone\":\"%s\", \"password\":\"%s\"}", 
                username, email, phone, password);
        
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
    }
}