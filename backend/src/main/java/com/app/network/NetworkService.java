package com.app.infrastructure.network;

public class NetworkService {

    public void callApi() {

        try {
            if (Math.random() > 0.5) {
                throw new RuntimeException("Timeout");
            }

        } catch (Exception e) {
            throw new RuntimeException("API call failed");
        }
    }
}