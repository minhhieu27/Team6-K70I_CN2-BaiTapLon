package com.app.service;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class WalletService {

    private static final String BASE_URL = "https://team6-k70i-cn2-baitaplon.onrender.com";

    private final HttpClient client = HttpClient.newHttpClient();

    private final Gson gson = new Gson();

    public CompletableFuture<HttpResponse<String>> getWallet(String token) {

        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/wallet"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .GET().build();
                
        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> deposit (String token, BigDecimal amount){

        JsonObject body = new JsonObject();

        body.addProperty("amount", amount);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/wallet/deposit"))
                                            .header("Authorization", "Bearer " + token)
                                            .header("Content-Type", "application/json")
                                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> withdraw(String token, BigDecimal amount){
        JsonObject body = new JsonObject();

        body.addProperty("amount", amount);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/wallet/withdraw"))
                                            .header("Authorization", "Bearer " + token)
                                            .header("Content-Type", "application/json")
                                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> getTransactions(String token){

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/wallet/transactions"))
                                            .header("Authorization", "Bearer " + token)
                                            .GET()
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}