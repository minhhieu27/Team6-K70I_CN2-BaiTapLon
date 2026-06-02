package server;

import shared.Response;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionServer {
    private static final int PORT = 9999;

    // Lưu toàn bộ client đang kết nối
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    // Lưu các client đang xem từng phiên đấu giá
    // Key: auctionId
    // Value: danh sách client đang xem auction đó
    private final ConcurrentHashMap<String, Set<ClientHandler>> auctionRooms =
            new ConcurrentHashMap<>();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Auction Server is running on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();

                ClientHandler clientHandler = new ClientHandler(socket, this);
                clients.add(clientHandler);
                clientHandler.start();

                System.out.println("New client connected: " + socket.getInetAddress());
                System.out.println("Current clients: " + clients.size());
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);

        // Khi client ngắt kết nối thì xóa khỏi tất cả auction room
        for (Set<ClientHandler> room : auctionRooms.values()) {
            room.remove(clientHandler);
        }

        System.out.println("Client removed. Current clients: " + clients.size());
    }

    public void joinAuctionRoom(String auctionId, ClientHandler clientHandler) {
        auctionRooms.putIfAbsent(auctionId, ConcurrentHashMap.newKeySet());
        auctionRooms.get(auctionId).add(clientHandler);

        System.out.println("Client joined auction room: " + auctionId);
    }

    public void leaveAuctionRoom(String auctionId, ClientHandler clientHandler) {
        Set<ClientHandler> room = auctionRooms.get(auctionId);

        if (room != null) {
            room.remove(clientHandler);
            System.out.println("Client left auction room: " + auctionId);
        }
    }

    public void broadcastToAuctionRoom(String auctionId, Response response) {
        Set<ClientHandler> room = auctionRooms.get(auctionId);

        if (room == null || room.isEmpty()) {
            System.out.println("No clients in auction room: " + auctionId);
            return;
        }

        for (ClientHandler client : room) {
            client.send(response);
        }

        System.out.println("Broadcast to auction room " + auctionId + ": "
                + response.getMessage());
    }

    public void broadcastToAll(Response response) {
        for (ClientHandler client : clients) {
            client.send(response);
        }

        System.out.println("Broadcast to all clients: " + response.getMessage());
    }

    public int getClientCount() {
        return clients.size();
    }

    public static void main(String[] args) {
        AuctionServer server = new AuctionServer();
        server.start();
    }
}