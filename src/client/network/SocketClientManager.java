package client.network;

public class SocketClientManager {

    private static AuctionClient client;

    private SocketClientManager() {
    }

    public static AuctionClient getClient() {
        if (client == null) {
            client = new AuctionClient();
        }

        return client;
    }

    public static void setCallback(SocketClientCallback callback) {
        getClient().setCallback(callback);
    }

    public static boolean connect() {
        AuctionClient auctionClient = getClient();

        if (auctionClient.isConnected()) {
            return true;
        }

        return auctionClient.connect("localhost", 9999);
    }

    public static void disconnect() {
        if (client != null) {
            client.disconnect();
        }
    }
}