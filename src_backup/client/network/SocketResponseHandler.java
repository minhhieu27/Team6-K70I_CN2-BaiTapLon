package client.network;

import shared.socket.dto.MessageType;
import shared.socket.dto.Response;

public class SocketResponseHandler {

    private final SocketClientCallback callback;

    public SocketResponseHandler(SocketClientCallback callback) {
        this.callback = callback;
    }

    public void handle(Response response) {
        if (response == null) {
            callback.onConnectionError("Response từ server bị null.");
            return;
        }

        if (!response.isSuccess()) {
            callback.onError(response);
            return;
        }

        MessageType type = response.getType();

        if (type == null) {
            callback.onSuccess(response);
            return;
        }

        switch (type) {
            case AUCTION_LIST:
                callback.onAuctionList(response);
                break;

            case AUCTION_DETAIL:
                callback.onAuctionDetail(response);
                break;

            case BID_HISTORY:
                callback.onBidHistory(response);
                break;

            case BID_UPDATE:
                callback.onBidUpdate(response);
                break;

            case SUCCESS:
                callback.onSuccess(response);
                break;

            case ERROR:
                callback.onError(response);
                break;

            default:
                callback.onSuccess(response);
                break;
        }
    }
}