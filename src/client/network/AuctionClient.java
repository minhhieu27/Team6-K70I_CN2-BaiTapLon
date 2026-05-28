package client.network;

import shared.socket.dto.MessageType;
import shared.socket.dto.Request;
import shared.socket.dto.Response;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AuctionClient {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    private ServerListener serverListener;
    private SocketResponseHandler responseHandler;

    private boolean connected = false;

    public AuctionClient() {
        this.responseHandler = new SocketResponseHandler();
    }

    public AuctionClient(SocketClientCallback callback) {
        this.responseHandler = new SocketResponseHandler(callback);
    }

    // =========================
    // CONNECTION
    // =========================

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);

            input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            output = new PrintWriter(socket.getOutputStream(), true);

            connected = true;

            serverListener = new ServerListener(input, responseHandler);
            serverListener.start();

            return true;

        } catch (Exception e) {
            connected = false;

            if (responseHandler != null) {
                responseHandler.handle(
                        Response.connectionError("Cannot connect to server: " + e.getMessage())
                );
            }

            return false;
        }
    }

    public void disconnect() {
        connected = false;

        try {
            if (serverListener != null) {
                serverListener.stopListening();
            }

            if (input != null) {
                input.close();
            }

            if (output != null) {
                output.close();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            if (responseHandler != null && responseHandler.getCallback() != null) {
                responseHandler.getCallback().onDisconnected();
            }

        } catch (Exception e) {
            if (responseHandler != null) {
                responseHandler.handle(
                        Response.connectionError("Disconnect error: " + e.getMessage())
                );
            }
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setCallback(SocketClientCallback callback) {
        if (responseHandler == null) {
            responseHandler = new SocketResponseHandler(callback);
        } else {
            responseHandler.setCallback(callback);
        }
    }

    // =========================
    // SEND REQUEST
    // =========================

    private void send(Request request) {
        if (!connected || output == null) {
            if (responseHandler != null) {
                responseHandler.handle(
                        Response.connectionError("Client is not connected to server")
                );
            }
            return;
        }

        if (request == null) {
            if (responseHandler != null) {
                responseHandler.handle(
                        Response.connectionError("Request is null")
                );
            }
            return;
        }

        output.println(request.toJson());
    }

    // =========================
    // AUTH
    // =========================

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

    public void logout() {
        send(Request.of(MessageType.LOGOUT));
        disconnect();
    }

    // =========================
    // AUCTION
    // =========================

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
                Request.mapOf("auctionId", auctionId)
        ));
    }

    public void createAuction(
            String title,
            String itemName,
            String description,
            double startPrice,
            String sellerId
    ) {
        send(new Request(
                MessageType.CREATE_AUCTION,
                Request.mapOf(
                        "title", title,
                        "itemName", itemName,
                        "description", description,
                        "startPrice", startPrice,
                        "sellerId", sellerId
                )
        ));
    }

    // =========================
    // REALTIME AUCTION ROOM
    // =========================

    public void joinAuction(String auctionId) {
        send(new Request(
                MessageType.JOIN_AUCTION,
                Request.mapOf("auctionId", auctionId)
        ));
    }

    public void leaveAuction(String auctionId) {
        send(new Request(
                MessageType.LEAVE_AUCTION,
                Request.mapOf("auctionId", auctionId)
        ));
    }

    // =========================
    // BIDDING
    // =========================

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
}