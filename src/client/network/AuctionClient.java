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
import shared.MessageType;
import shared.Request;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class AuctionClient {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private ServerListener serverListener;
    private ClientMessageHandler messageHandler;

    private boolean connected = false;

    public AuctionClient() {
        this.messageHandler = new ClientMessageHandler();
    }

    public AuctionClient(ClientMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);

<<<<<<< HEAD
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
=======
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();

            input = new ObjectInputStream(socket.getInputStream());

            serverListener = new ServerListener(input, messageHandler);
            serverListener.start();

            connected = true;

            System.out.println("Connected to server: " + host + ":" + port);
            return true;

        } catch (Exception e) {
            connected = false;
            System.out.println("Connect error: " + e.getMessage());
>>>>>>> 9b400298307a283b9dda72788ed460b6d762656f
            return false;
        }
    }

<<<<<<< HEAD
    public void disconnect() {
        try {
            if (serverListener != null) {
                serverListener.stop();
=======
    public void sendRequest(Request request) {
        if (!connected || output == null) {
            System.out.println("Client is not connected to server");
            return;
        }

        try {
            output.writeObject(request);
            output.flush();
            output.reset();
        } catch (Exception e) {
            System.out.println("Send request error: " + e.getMessage());
        }
    }

    public void login(String username, String password) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);

        Request request = new Request(MessageType.LOGIN, data);
        sendRequest(request);
    }

    public void register(String username, String password, String role) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);
        data.put("role", role);

        Request request = new Request(MessageType.REGISTER, data);
        sendRequest(request);
    }

    public void getAuctions() {
        Request request = new Request(MessageType.GET_AUCTIONS, null);
        sendRequest(request);
    }

    public void getAuctionDetail(String auctionId) {
        Map<String, Object> data = new HashMap<>();
        data.put("auctionId", auctionId);

        Request request = new Request(MessageType.GET_AUCTION_DETAIL, data);
        sendRequest(request);
    }

    public void joinAuction(String auctionId) {
        Map<String, Object> data = new HashMap<>();
        data.put("auctionId", auctionId);

        Request request = new Request(MessageType.JOIN_AUCTION, data);
        sendRequest(request);
    }

    public void leaveAuction(String auctionId) {
        Map<String, Object> data = new HashMap<>();
        data.put("auctionId", auctionId);

        Request request = new Request(MessageType.LEAVE_AUCTION, data);
        sendRequest(request);
    }

    public void placeBid(String auctionId, String bidderId, double price) {
        Map<String, Object> data = new HashMap<>();
        data.put("auctionId", auctionId);
        data.put("bidderId", bidderId);
        data.put("price", price);

        Request request = new Request(MessageType.PLACE_BID, data);
        sendRequest(request);
    }

    public void getBidHistory(String auctionId) {
        Map<String, Object> data = new HashMap<>();
        data.put("auctionId", auctionId);

        Request request = new Request(MessageType.GET_BID_HISTORY, data);
        sendRequest(request);
    }

    public void createAuction(Map<String, Object> auctionData) {
        Request request = new Request(MessageType.CREATE_AUCTION, auctionData);
        sendRequest(request);
    }

    public void updateAuction(Map<String, Object> auctionData) {
        Request request = new Request(MessageType.UPDATE_AUCTION, auctionData);
        sendRequest(request);
    }

    public void deleteAuction(String auctionId) {
        Map<String, Object> data = new HashMap<>();
        data.put("auctionId", auctionId);

        Request request = new Request(MessageType.DELETE_AUCTION, data);
        sendRequest(request);
    }

    public void logout() {
        Request request = new Request(MessageType.LOGOUT, null);
        sendRequest(request);
        disconnect();
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
>>>>>>> 9b400298307a283b9dda72788ed460b6d762656f
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

<<<<<<< HEAD
        } catch (Exception e) {
            callback.onConnectionError("Error while disconnecting: " + e.getMessage());
=======
            System.out.println("Disconnected from server");

        } catch (Exception e) {
            System.out.println("Disconnect error: " + e.getMessage());
>>>>>>> 9b400298307a283b9dda72788ed460b6d762656f
        }
    }

    public boolean isConnected() {
<<<<<<< HEAD
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
=======
        return connected;
    }

    public void setMessageHandler(ClientMessageHandler messageHandler) {
        this.messageHandler = messageHandler;

        if (serverListener != null) {
            serverListener.setMessageHandler(messageHandler);
        }
>>>>>>> 9b400298307a283b9dda72788ed460b6d762656f
    }
}