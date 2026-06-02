package com.app.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ImageService {
    
    private static final String BASE_URL = "https://team6-k70i-cn2-baitaplon.onrender.com";

    private final HttpClient client = HttpClient.newHttpClient();

    public CompletableFuture<HttpResponse<String>> uploadImage(Path path) throws IOException{

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/images/upload"))
                                            .header("Content-Type", "multipart/form-data")
                                            .POST(HttpRequest.BodyPublishers.ofFile(path))
                                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
