package com.app.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class NotificationService {
    
    private static final String BASE_URL = "http://localhost:8080";

    private final HttpClient client = HttpClient.newHttpClient();

    public CompletableFuture<HttpResponse<String>> getNotifications(String token){

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/notifications"))
                                            .header("Authorization", "Bearer " + token)
                                            .GET()
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> markAsRead(String token, String notificationId){

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/notifications/" + notificationId + "/read"))
                                            .header("Authorization", "Bearer " + token)
                                            .PUT(HttpRequest.BodyPublishers.noBody())
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> readAll(String token){

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/notifications/read-all"))
                                            .header("Authorization", "Bearer " + token)
                                            .PUT(HttpRequest.BodyPublishers.noBody())
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
