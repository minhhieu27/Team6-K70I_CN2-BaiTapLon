package client.network;

public class SocketClientManager {

    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    private static AuctionClient client;
    private static SocketClientCallback callback;

    private SocketClientManager() {
        // Không cho tạo object từ class manager
    }

    public static void setCallback(SocketClientCallback newCallback) {
        callback = newCallback;

        // Nếu đổi callback thì tạo lại client mới để callback được cập nhật
        client = new AuctionClient(callback);
    }

    public static AuctionClient getClient() {
        if (callback == null) {
            throw new IllegalStateException(
                    "SocketClientCallback chưa được set. Hãy gọi SocketClientManager.setCallback(...) trước."
            );
        }

        if (client == null) {
            client = new AuctionClient(callback);
        }

        return client;
    }

    public static boolean connect() {
        AuctionClient auctionClient = getClient();

        if (auctionClient.isConnected()) {
            return true;
        }

        return auctionClient.connect(HOST, PORT);
    }

    public static void disconnect() {
        if (client != null) {
            client.disconnect();
        }
    }

    public static boolean isConnected() {
        return client != null && client.isConnected();
    }

    public static void loadAuctions() {
        if (connect()) {
            client.getAuctions();
        }
    }

    public static void getAuctionDetail(String auctionId) {
        if (connect()) {
            client.getAuctionDetail(auctionId);
        }
    }

    public static void joinAuction(String auctionId) {
        if (connect()) {
            client.joinAuction(auctionId);
        }
    }

    public static void leaveAuction(String auctionId) {
        if (connect()) {
            client.leaveAuction(auctionId);
        }
    }

    public static void getBidHistory(String auctionId) {
        if (connect()) {
            client.getBidHistory(auctionId);
        }
    }

    public static void placeBid(String auctionId, String userId, double price) {
        if (connect()) {
            client.placeBid(auctionId, userId, price);
        }
    }
}