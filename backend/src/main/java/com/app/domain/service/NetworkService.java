package com.app.domain.service;

import com.app.domain.exception.NetworkError.NetworkException;

public class NetworkService {

    public void callApi() throws NetworkException {

        try {
            if (Math.random() > 0.5) {
                throw new RuntimeException("Timeout");
            }

        } catch (Exception e) {
            throw new NetworkException("API call failed");
        }
    }
}