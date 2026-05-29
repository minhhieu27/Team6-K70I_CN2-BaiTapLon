package client.network;

import shared.socket.dto.Response;

public interface SocketClientCallback {

    void onAuctionList(Response response);

    void onAuctionDetail(Response response);

    void onBidHistory(Response response);

    void onBidUpdate(Response response);

    void onSuccess(Response response);

    void onError(Response response);

    void onConnectionError(String message);
}