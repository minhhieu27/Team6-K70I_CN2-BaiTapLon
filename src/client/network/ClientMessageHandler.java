package client.network;

import shared.MessageType;
import shared.Response;

public class ClientMessageHandler {

    public void handle(Response response) {
        if (response == null) {
            System.out.println("Response is null");
            return;
        }

        MessageType type = response.getType();

        if (type == null) {
            System.out.println("Response type is null");
            return;
        }

        switch (type) {
            case SUCCESS:
                handleSuccess(response);
                break;

            case ERROR:
                handleError(response);
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

            case AUCTION_STARTED:
                handleAuctionStarted(response);
                break;

            case AUCTION_FINISHED:
                handleAuctionFinished(response);
                break;

            case AUCTION_CANCELED:
                handleAuctionCanceled(response);
                break;

            default:
                handleDefault(response);
                break;
        }
    }

    protected void handleSuccess(Response response) {
        System.out.println("[SUCCESS] " + response.getMessage());
        System.out.println("Data: " + response.getData());
    }

    protected void handleError(Response response) {
        System.out.println("[ERROR] " + response.getMessage());
        System.out.println("Data: " + response.getData());
    }

    protected void handleAuctionList(Response response) {
        System.out.println("[AUCTION_LIST] " + response.getMessage());
        System.out.println("Auctions: " + response.getData());
    }

    protected void handleAuctionDetail(Response response) {
        System.out.println("[AUCTION_DETAIL] " + response.getMessage());
        System.out.println("Auction detail: " + response.getData());
    }

    protected void handleBidUpdate(Response response) {
        System.out.println("[BID_UPDATE - REALTIME] " + response.getMessage());
        System.out.println("Bid data: " + response.getData());

        /*
         * Sau này ở JavaFX:
         * - Cập nhật currentPrice
         * - Cập nhật highestBidder
         * - Cập nhật biểu đồ giá
         */
    }

    protected void handleBidHistory(Response response) {
        System.out.println("[BID_HISTORY] " + response.getMessage());
        System.out.println("History: " + response.getData());
    }

    protected void handleAuctionStatusUpdate(Response response) {
        System.out.println("[AUCTION_STATUS_UPDATE] " + response.getMessage());
        System.out.println("Status data: " + response.getData());
    }

    protected void handleAuctionStarted(Response response) {
        System.out.println("[AUCTION_STARTED] " + response.getMessage());
        System.out.println("Data: " + response.getData());
    }

    protected void handleAuctionFinished(Response response) {
        System.out.println("[AUCTION_FINISHED] " + response.getMessage());
        System.out.println("Result: " + response.getData());
    }

    protected void handleAuctionCanceled(Response response) {
        System.out.println("[AUCTION_CANCELED] " + response.getMessage());
        System.out.println("Data: " + response.getData());
    }

    protected void handleDefault(Response response) {
        System.out.println("[UNKNOWN RESPONSE]");
        System.out.println("Type: " + response.getType());
        System.out.println("Message: " + response.getMessage());
        System.out.println("Data: " + response.getData());
    }
}