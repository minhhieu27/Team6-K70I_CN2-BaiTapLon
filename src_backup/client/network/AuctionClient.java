package client.network;

import shared.socket.dto.MessageType;
import shared.socket.dto.Request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class AuctionClient {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private Thread listenerThread;
    private ServerListener serverListener;

    private final SocketClientCallback callback;
    private final SocketResponseHandler responseHandler;

    public AuctionClient(SocketClientCallback callback) {
        this.callback = callback;
        this.responseHandler = new SocketResponseHandler(callback);
    }

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);

            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );

            writer = new PrintWriter(socket.getOutputStream(), true);

            serverListener = new ServerListener(reader, responseHandler);
            listenerThread = new Thread(serverListener);
            listenerThread.setDaemon(true);
            listenerThread.start();

            return true;

        } catch (Exception e) {
            callback.onConnectionError("Cannot connect to server: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        try {
            if (serverListener != null) {
                serverListener.stop();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

        } catch (Exception e) {
            callback.onConnectionError("Error while disconnecting: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return socket != null
                && socket.isConnected()
                && !socket.isClosed()
                && writer != null;
    }

    public void send(Request request) {
        if (!isConnected()) {
            callback.onConnectionError("Client is not connected to server");
            return;
        }

        try {
            writer.println(request.toJson());
            writer.flush();

        } catch (Exception e) {
            callback.onConnectionError("Cannot send request: " + e.getMessage());
        }
    }

    public void login(String identifier, String password) {
        send(new Request(
                MessageType.LOGIN,
                Request.mapOf(
                        "identifier", identifier,
                        "password", password
                )
        ));
    }

    public void register(String username, String email, String phone, String password) {
        send(new Request(
                MessageType.REGISTER,
                Request.mapOf(
                        "username", username,
                        "email", email,
                        "phone", phone,
                        "password", password
                )
        ));
    }

    public void getAuctions() {
        send(new Request(
                MessageType.GET_AUCTIONS,
                Request.mapOf(
                        "page", 0,
                        "size", 20
                )
        ));
    }

    public void getAuctionDetail(String auctionId) {
        send(new Request(
                MessageType.GET_AUCTION_DETAIL,
                Request.mapOf(
                        "auctionId", auctionId
                )
        ));
    }

    public void createAuction(
            String title,
            String itemName,
            String description,
            String sellerId,
            double startPrice
    ) {
        send(new Request(
                MessageType.CREATE_AUCTION,
                Request.mapOf(
                        "title", title,
                        "itemName", itemName,
                        "description", description,
                        "sellerId", sellerId,
                        "startPrice", startPrice
                )
        ));
    }

    public void joinAuction(String auctionId) {
        send(new Request(
                MessageType.JOIN_AUCTION,
                Request.mapOf(
                        "auctionId", auctionId
                )
        ));
    }

    public void leaveAuction(String auctionId) {
        send(new Request(
                MessageType.LEAVE_AUCTION,
                Request.mapOf(
                        "auctionId", auctionId
                )
        ));
    }

    public void getBidHistory(String auctionId) {
        send(new Request(
                MessageType.GET_BID_HISTORY,
                Request.mapOf(
                        "auctionId", auctionId,
                        "page", 0,
                        "size", 20
                )
        ));
    }

    public void placeBid(String auctionId, String userId, double price) {
        send(new Request(
                MessageType.PLACE_BID,
                Request.mapOf(
                        "auctionId", auctionId,
                        "userId", userId,
                        "price", price
                )
        ));
    }

    public void logout() {
        send(Request.of(MessageType.LOGOUT));
    }
}