package com.app.socket;

import com.app.socket.dto.Response;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuctionSocketServer {

    private static final int PORT = 9999;

    private final SocketRequestProcessor requestProcessor;

    private final Set<ClientConnectionHandler> clients = ConcurrentHashMap.newKeySet();

    /*
     * Key: auctionId
     * Value: danh sách client đang xem phiên đấu giá đó
     */
    private final ConcurrentHashMap<String, Set<ClientConnectionHandler>> auctionRooms =
            new ConcurrentHashMap<>();

    public AuctionSocketServer(SocketRequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    /*
     * Khi Spring Boot chạy, hàm này tự động được gọi.
     * Server socket sẽ chạy ở một thread riêng.
     */
    @PostConstruct
    public void startInBackground() {
        Thread serverThread = new Thread(this::start);
        serverThread.setName("auction-socket-server-thread");
        serverThread.setDaemon(false);
        serverThread.start();
    }

    private void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Auction Socket JSON Server is running on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();

                ClientConnectionHandler handler =
                        new ClientConnectionHandler(socket, this, requestProcessor);

                clients.add(handler);
                handler.start();

                System.out.println("New socket client connected: " + socket.getInetAddress());
                System.out.println("Current socket clients: " + clients.size());
            }

        } catch (IOException e) {
            System.out.println("Socket server error: " + e.getMessage());
        }
    }

    public void removeClient(ClientConnectionHandler handler) {
        clients.remove(handler);

        for (Set<ClientConnectionHandler> room : auctionRooms.values()) {
            room.remove(handler);
        }

        System.out.println("Socket client removed. Current clients: " + clients.size());
    }

    public void joinAuctionRoom(String auctionId, ClientConnectionHandler handler) {
        if (auctionId == null || auctionId.trim().isEmpty() || handler == null) {
            return;
        }

        auctionRooms.putIfAbsent(auctionId, ConcurrentHashMap.newKeySet());
        auctionRooms.get(auctionId).add(handler);

        System.out.println("Client joined auction room: " + auctionId);
    }

    public void leaveAuctionRoom(String auctionId, ClientConnectionHandler handler) {
        if (auctionId == null || auctionId.trim().isEmpty() || handler == null) {
            return;
        }

        Set<ClientConnectionHandler> room = auctionRooms.get(auctionId);

        if (room != null) {
            room.remove(handler);
            System.out.println("Client left auction room: " + auctionId);
        }
    }

    public void broadcastToAuctionRoom(String auctionId, Response response) {
        if (auctionId == null || response == null) {
            return;
        }

        Set<ClientConnectionHandler> room = auctionRooms.get(auctionId);

        if (room == null || room.isEmpty()) {
            System.out.println("No client in auction room: " + auctionId);
            return;
        }

        for (ClientConnectionHandler client : room) {
            client.send(response);
        }

        System.out.println("Broadcast to auction room " + auctionId + ": " + response.getMessage());
    }

    public void broadcastToAll(Response response) {
        if (response == null) {
            return;
        }

        for (ClientConnectionHandler client : clients) {
            client.send(response);
        }
    }

    public int getClientCount() {
        return clients.size();
    }
}
