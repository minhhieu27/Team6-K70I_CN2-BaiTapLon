package com.app;
import java.net.URI;
import java.net.http.*;
import java.util.concurrent.CompletableFuture;

public class WalletService {
    private final HttpClient client = HttpClient.newHttpClient();
    public CompletableFuture<HttpResponse<String>> getWallet(String token) {
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/wallet"))
                .header("Authorization", "Bearer " + token).GET().build();
        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
    }
}