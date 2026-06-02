package client.ui;

import client.network.AuctionClient;
import client.network.SocketClientCallback;
import javafx.application.Platform;
import shared.socket.dto.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SocketUiBridge implements SocketClientCallback {

    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    private static AuctionClient client;
    private static SocketUiBridge instance;
    private static AuctionUiListener uiListener;

    private SocketUiBridge() {
    }

    public static void setListener(AuctionUiListener listener) {
        uiListener = listener;
    }

    public static void connect() {
        if (instance == null) {
            instance = new SocketUiBridge();
        }

        if (client == null) {
            client = new AuctionClient(instance);
        }

        if (!client.isConnected()) {
            boolean connected = client.connect(HOST, PORT);

            if (connected) {
                System.out.println("[SOCKET] Connected to server " + HOST + ":" + PORT);
            } else {
                notifyError("Không thể kết nối tới socket server " + HOST + ":" + PORT);
            }
        }
    }

    public static void disconnect() {
        if (client != null) {
            client.disconnect();
        }
    }

    public static boolean isConnected() {
        return client != null && client.isConnected();
    }

    private static boolean ensureConnected() {
        if (client == null || !client.isConnected()) {
            connect();
        }

        if (client == null || !client.isConnected()) {
            notifyError("Client chưa kết nối tới server.");
            return false;
        }

        return true;
    }

    public static void loadAuctions() {
        if (!ensureConnected()) {
            return;
        }

        client.getAuctions();
    }

    public static void getAuctionDetail(String auctionId) {
        if (!ensureConnected()) {
            return;
        }

        client.getAuctionDetail(auctionId);
    }

    public static void joinAuction(String auctionId) {
        if (!ensureConnected()) {
            return;
        }

        client.joinAuction(auctionId);
    }

    public static void leaveAuction(String auctionId) {
        if (!ensureConnected()) {
            return;
        }

        client.leaveAuction(auctionId);
    }

    public static void getBidHistory(String auctionId) {
        if (!ensureConnected()) {
            return;
        }

        client.getBidHistory(auctionId);
    }

    public static void placeBid(String auctionId, String userId, double price) {
        if (!ensureConnected()) {
            return;
        }

        client.placeBid(auctionId, userId, price);
    }

    @Override
    public void onAuctionList(Response response) {
        System.out.println("[AUCTION LIST]");
        System.out.println(response.getData());

        List<Map<String, Object>> auctions = extractContentList(response.getData());

        Platform.runLater(() -> {
            if (uiListener != null) {
                uiListener.onAuctionList(auctions);
            }
        });
    }

    @Override
    public void onAuctionDetail(Response response) {
        System.out.println("[AUCTION DETAIL]");
        System.out.println(response.getData());

        Map<String, Object> auction = castMap(response.getData());

        Platform.runLater(() -> {
            if (uiListener != null) {
                uiListener.onAuctionDetail(auction);
            }
        });
    }

    @Override
    public void onBidHistory(Response response) {
        System.out.println("[BID HISTORY]");
        System.out.println(response.getData());

        List<Map<String, Object>> bids = extractContentList(response.getData());

        Platform.runLater(() -> {
            if (uiListener != null) {
                uiListener.onBidHistory(bids);
            }
        });
    }

    @Override
    public void onBidUpdate(Response response) {
        System.out.println("[BID UPDATE - REALTIME]");
        System.out.println(response.getData());

        Map<String, Object> bidUpdate = castMap(response.getData());

        Platform.runLater(() -> {
            if (uiListener != null) {
                uiListener.onBidUpdate(bidUpdate);
            }
        });
    }

    @Override
    public void onSuccess(Response response) {
        System.out.println("[SOCKET SUCCESS] " + response.getMessage());
        System.out.println("Data: " + response.getData());

        Platform.runLater(() -> {
            if (uiListener != null) {
                uiListener.onSuccess(response.getMessage(), response.getData());
            }
        });
    }

    @Override
    public void onError(Response response) {
        System.out.println("[SOCKET ERROR] " + response.getMessage());
        System.out.println("Data: " + response.getData());

        Platform.runLater(() -> {
            if (uiListener != null) {
                uiListener.onError(response.getMessage());
            }
        });
    }

    @Override
    public void onConnectionError(String message) {
        System.out.println("[SOCKET CONNECTION ERROR] " + message);

        Platform.runLater(() -> {
            if (uiListener != null) {
                uiListener.onError(message);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Object data) {
        if (data instanceof Map<?, ?>) {
            return (Map<String, Object>) data;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> extractContentList(Object data) {
        if (!(data instanceof Map<?, ?> mapData)) {
            return new ArrayList<>();
        }

        Object content = mapData.get("content");

        if (content instanceof List<?>) {
            return (List<Map<String, Object>>) content;
        }

        return new ArrayList<>();
    }

    private static void notifyError(String message) {
        System.out.println("[SOCKET ERROR] " + message);

        Platform.runLater(() -> {
            if (uiListener != null) {
                uiListener.onError(message);
            }
        });
    }
}