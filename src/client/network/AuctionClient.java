package client.network;

import shared.socket.dto.MessageType;
import shared.socket.dto.Request;
import shared.socket.dto.Response;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AuctionClient {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread listenerThread;
    private boolean connected = false;
    private final Gson gson = new Gson();

    private SocketClientCallback callback;

    public AuctionClient(SocketClientCallback callback) {
        this.callback = callback;
    }

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new PrintWriter(socket.getOutputStream(), true);
            connected = true;

            // Chạy luồng lắng nghe server
            listenerThread = new Thread(this::listenToServer);
            listenerThread.setDaemon(true);
            listenerThread.start();

            System.out.println("Đã kết nối Socket tới Server: " + host + ":" + port);
            return true;
        } catch (Exception e) {
            if(callback != null) callback.onConnectionError("Lỗi kết nối: " + e.getMessage());
            return false;
        }
    }

    private void listenToServer() {
        try {
            String line;
            while (connected && (line = reader.readLine()) != null) {
                Response response = Response.fromJson(line);
                if (callback != null) {
                    handleResponse(response);
                }
            }
        } catch (Exception e) {
            if(callback != null && connected) callback.onConnectionError("Mất kết nối server!");
        } finally {
            disconnect();
        }
    }

    private void handleResponse(Response response) {
        if (!response.isSuccess()) {
            callback.onError(response);
            return;
        }
        switch (response.getType()) {
            case AUCTION_LIST -> callback.onAuctionList(response);
            case AUCTION_DETAIL -> callback.onAuctionDetail(response);
            case BID_UPDATE -> callback.onBidUpdate(response);
            case BID_HISTORY -> callback.onBidHistory(response);
            default -> callback.onSuccess(response);
        }
    }

    public void send(Request request) {
        if (!connected || writer == null) {
            if(callback != null) callback.onConnectionError("Chưa kết nối tới server");
            return;
        }
        writer.println(request.toJson());
        writer.flush();
    }

    // Các hàm tiện ích gọi Server
    public void joinAuction(String auctionId) {
        send(new Request(MessageType.JOIN_AUCTION, Request.mapOf("auctionId", auctionId)));
    }

    public void placeBid(String auctionId, String userId, double price) {
        send(new Request(MessageType.PLACE_BID, Request.mapOf("auctionId", auctionId, "userId", userId, "price", price)));
    }

    public void leaveAuction(String auctionId) {
        send(new Request(MessageType.LEAVE_AUCTION, Request.mapOf("auctionId", auctionId)));
    }

    public void disconnect() {
        connected = false;
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (Exception e) {
            System.out.println("Lỗi ngắt kết nối: " + e.getMessage());
        }
    }

    public boolean isConnected() { return connected; }
}