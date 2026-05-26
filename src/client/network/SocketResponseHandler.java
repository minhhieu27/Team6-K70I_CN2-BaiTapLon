package client.network;

import shared.socket.dto.MessageType;
import shared.socket.dto.Response;

public class SocketResponseHandler {

    private SocketClientCallback callback;

    public SocketResponseHandler() {
    }

    public SocketResponseHandler(SocketClientCallback callback) {
        this.callback = callback;
    }

    public void handle(Response response) {
        if (response == null) {
            if (callback != null) {
                callback.onConnectionError("Response from server is null");
            }
            return;
        }

        MessageType type = response.getType();

        if (type == null) {
            if (callback != null) {
                callback.onConnectionError("Response type is null");
            }
            return;
        }

        switch (type) {
            case SUCCESS:
                handleSuccess(response);
                break;

            case ERROR:
                handleError(response);
                break;

            case CONNECTION_ERROR:
                handleConnectionError(response);
                break;

            case AUCTION_LIST:
                handleAuctionList(response);
                break;

            case AUCTION_DETAIL:
                handleAuctionDetail(response);
                break;

            case BID_UPDATE:
                handleBidUpdate(response);
                break;

            case BID_HISTORY:
                handleBidHistory(response);
                break;

            case AUCTION_STATUS_UPDATE:
                handleAuctionStatusUpdate(response);
                break;

            case AUCTION_FINISHED:
                handleAuctionFinished(response);
                break;

            default:
                handleDefault(response);
                break;
        }
    }

    private void handleSuccess(Response response) {
        if (callback != null) {
            callback.onSuccess(response);
        }
    }

    private void handleError(Response response) {
        if (callback != null) {
            callback.onError(response);
        }
    }

    private void handleConnectionError(Response response) {
        if (callback != null) {
            callback.onConnectionError(response.getMessage());
        }
    }

    private void handleAuctionList(Response response) {
        if (callback != null) {
            callback.onAuctionList(response);
        }
    }

    private void handleAuctionDetail(Response response) {
        if (callback != null) {
            callback.onAuctionDetail(response);
        }
    }

    private void handleBidUpdate(Response response) {
        if (callback != null) {
            callback.onBidUpdate(response);
        }
    }

    private void handleBidHistory(Response response) {
        if (callback != null) {
            callback.onBidHistory(response);
        }
    }

    private void handleAuctionStatusUpdate(Response response) {
        if (callback != null) {
            callback.onAuctionStatusUpdate(response);
        }
    }

    private void handleAuctionFinished(Response response) {
        if (callback != null) {
            callback.onAuctionFinished(response);
        }
    }

    private void handleDefault(Response response) {
        /*
         * Nếu server trả về type chưa xử lý riêng,
         * tạm coi là response thành công chung.
         */
        if (callback != null) {
            callback.onSuccess(response);
        }
    }

    public SocketClientCallback getCallback() {
        return callback;
    }

    public void setCallback(SocketClientCallback callback) {
        this.callback = callback;
    }
}