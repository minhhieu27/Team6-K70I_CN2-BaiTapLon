package com.app;
import java.net.URI;
import java.net.http.*;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    private final HttpClient client = HttpClient.newHttpClient();
    
    public CompletableFuture<HttpResponse<String>> login(String u, String p) {
        // FIX LỖI Ở ĐÂY: Đổi "username" thành "identifier" để khớp với Backend của Hiếu
        String json = String.format("{\"identifier\":\"%s\", \"password\":\"%s\"}", u, p);
        
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/auth/login"))
                .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
    }
}