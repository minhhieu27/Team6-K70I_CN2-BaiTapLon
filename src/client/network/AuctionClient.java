package client.network;

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
            return false;
        }
    }

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
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            System.out.println("Disconnected from server");

        } catch (Exception e) {
            System.out.println("Disconnect error: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setMessageHandler(ClientMessageHandler messageHandler) {
        this.messageHandler = messageHandler;

        if (serverListener != null) {
            serverListener.setMessageHandler(messageHandler);
        }
    }
}