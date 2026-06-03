package com.app.service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class AuctionService {

    private static final String BASE_URL = "https://team6-k70i-cn2-baitaplon.onrender.com";

    private final HttpClient client = HttpClient.newHttpClient();

    private final Gson gson = new Gson();

    public CompletableFuture<HttpResponse<String>> createAuction(String token, String json) {

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/auctions/create"))
                                        .header("Content-Type", "application/json")
                                        .header("Authorization", "Bearer " + token)
                                        .POST(HttpRequest.BodyPublishers.ofString(json))
                                        .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> getAllAuctions(int page, int size) {

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create( BASE_URL + "/auctions?page=" + page + "&size=" + size))
                                            .GET()
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
    
    public CompletableFuture<HttpResponse<String>> getAuctionById(String auctionId){

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/auctions/" + auctionId))
                                            .header("Content-Type", "application/json")
                                            .GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> getAuctionBySeller(String sellerId){

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/auctions/seller/" + sellerId))
                                            .header("Content-Type", "application/json")
                                            .GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> deleteAuction(String token, String auctionId) {

        HttpRequest request =
                HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/admin/auction/" + auctionId))
                            .header("Authorization", "Bearer " + token)
                            .DELETE()
                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> followAuction(String token, String auctionId){

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/auctions/" + auctionId + "/follow"))
                                            .header("Authorization", "Bearer " + token)
                                            .POST(HttpRequest.BodyPublishers.noBody())
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> unfollowAuction(String token, String auctionId){

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/auctions/" + auctionId + "/unfollow"))
                                            .header("Authorization", "Bearer " + token)
                                            .POST(HttpRequest.BodyPublishers.noBody())
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> createAutoBid(String token, String auctionId, BigDecimal maxAmount){

        JsonObject body = new JsonObject();

        body.addProperty("auctionId", auctionId);
        body.addProperty("maxAmount", maxAmount);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/autobids"))
                                            .header("Authorization", "Bearer " + token)
                                            .header("Content-Type", "application/json")
                                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                                            .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> getBidHistory(String token, String auctionId){

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/bids" + auctionId + "/history"))
                                            .header("Authorization", "Bearer " + token)
                                            .GET()
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
