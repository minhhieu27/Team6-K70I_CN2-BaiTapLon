package client.ui;

import client.network.AuctionClient;
import client.network.SocketClientCallback;
import shared.socket.dto.Response;

public class SocketUiBridge {

    private static AuctionClient client;

    private SocketUiBridge() {
    }

    public static void init() {
        if (client != null && client.isConnected()) {
            return;
        }

        client = new AuctionClient(new SocketClientCallback() {
            @Override
            public void onSuccess(Response response) {
                System.out.println("[SOCKET SUCCESS] " + response.getMessage());
                System.out.println("Data: " + response.getData());
            }

            @Override
            public void onError(Response response) {
                System.out.println("[SOCKET ERROR] " + response.getMessage());
                System.out.println("Data: " + response.getData());
            }

            @Override
            public void onAuctionList(Response response) {
                System.out.println("[AUCTION LIST]");
                System.out.println(response.getData());
            }

            @Override
            public void onAuctionDetail(Response response) {
                System.out.println("[AUCTION DETAIL]");
                System.out.println(response.getData());
            }

            @Override
            public void onBidUpdate(Response response) {
                System.out.println("[BID UPDATE - REALTIME]");
                System.out.println(response.getData());
            }

            @Override
            public void onBidHistory(Response response) {
                System.out.println("[BID HISTORY]");
                System.out.println(response.getData());
            }

            @Override
            public void onAuctionStatusUpdate(Response response) {
                System.out.println("[AUCTION STATUS UPDATE]");
                System.out.println(response.getData());
            }

            @Override
            public void onAuctionFinished(Response response) {
                System.out.println("[AUCTION FINISHED]");
                System.out.println(response.getData());
            }

            @Override
            public void onConnectionError(String message) {
                System.out.println("[SOCKET CONNECTION ERROR] " + message);
            }

            @Override
            public void onDisconnected() {
                System.out.println("[SOCKET DISCONNECTED]");
            }
        });

        boolean connected = client.connect("localhost", 9999);

        if (connected) {
            System.out.println("[SOCKET] Connected to server localhost:9999");
        } else {
            System.out.println("[SOCKET] Failed to connect to server localhost:9999");
        }
    }

    public static AuctionClient getClient() {
        if (client == null) {
            init();
        }

        return client;
    }

    public static void loadAuctions() {
        getClient().getAuctions();
    }

    public static void joinAuction(String auctionId) {
        getClient().joinAuction(auctionId);
        getClient().getAuctionDetail(auctionId);
        getClient().getBidHistory(auctionId);
    }

    public static void placeBid(String auctionId, String userId, double price) {
        getClient().placeBid(auctionId, userId, price);
    }

    public static void disconnect() {
        if (client != null) {
            client.disconnect();
        }
    }
}