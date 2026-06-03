package com.app.service;

import java.net.URI;
import java.net.http.*;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class AuthService {

    private static final String BASE_URL = "http://localhost:8080";

    private final Gson gson = new Gson();

    private final HttpClient client = HttpClient.newHttpClient();
    
    
    // Gọi API Đăng nhập
    public CompletableFuture<HttpResponse<String>> login(String identifier, String password) {

        JsonObject body = new JsonObject();
        
        body.addProperty("identifier", identifier);
        body.addProperty("password", password);
        
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .build();

        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
    }

    // Gọi API Đăng ký
    public CompletableFuture<HttpResponse<String>> register(String username, String email, String phone, String password) {

        JsonObject body = new JsonObject();

        body.addProperty("username", username);
        body.addProperty("email", email);
        body.addProperty("phone", phone);
        body.addProperty("password", password);
        
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .build();

        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> forgotPassword(String email) {

        JsonObject body = new JsonObject();

        body.addProperty("email", email);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/auth/forgot-password"))
                                            .header("Content-Type", "application/json")
                                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}