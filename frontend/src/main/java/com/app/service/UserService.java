package com.app.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class UserService {
    
    private final HttpClient client = HttpClient.newHttpClient();

    private final Gson gson = new Gson();

    private static final String BASE_URL = "http://localhost:8080";

    public CompletableFuture<HttpResponse<String>> getProfile(String token){

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL +"/users/me"))
                                            .header("Authorization", "Bearer " + token)
                                            .header("Content-Type", "application/json")
                                            .GET()
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> updateProfile(String token, String fullName, String phone, String address, String email){
        JsonObject body = new JsonObject();

        body.addProperty("fullName", fullName);
        body.addProperty("phone", phone);
        body.addProperty("email", email);
        body.addProperty("address", address);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/users/profile"))
                                            .header("Authorization", "Bearer " + token)
                                            .header("Content-Type", "application/json")
                                            .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> changePassword(String token, String oldPassword, String newPassword) {

        JsonObject body = new JsonObject();

        body.addProperty("oldPassword", oldPassword);
        body.addProperty("newPassword", newPassword);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/users/change-password"))
                                            .header("Authorization", "Bearer " + token)
                                            .header( "Content-Type", "application/json")
                                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> resetPassword(String identifier) {

        JsonObject body = new JsonObject();

        body.addProperty("identifier", identifier);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/users/reset-password"))
                                            .header("Content-Type", "application/json")
                                            .PUT(HttpRequest.BodyPublishers.ofString(identifier))
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
