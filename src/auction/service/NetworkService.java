package auction.service;

import auction.exception.NetworkException;

public class NetworkService {

    public void callApi() throws NetworkException {

        try {
            if (Math.random() > 0.5) {
                throw new RuntimeException("Timeout");
            }

        } catch (Exception e) {
            throw new NetworkService("API call failed");
        }
    }
}