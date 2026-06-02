package com.app;

import com.google.gson.JsonObject;
import javafx.application.Platform; // CÁI DÒNG NÀY ĐỂ HẾT BÁO ĐỎ NÀY
import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class SocketClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void placeBid(String auctionId, double price) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "PLACE_BID");
        JsonObject data = new JsonObject();
        data.addProperty("auctionId", auctionId);
        data.addProperty("price", price);
        json.add("data", data);
        out.println(json.toString());
    }

    public void listen(Consumer<String> onMessage) {
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) onMessage.accept(line);
            } catch (IOException e) {
                Platform.runLater(() -> onMessage.accept("Mất kết nối với Socket Server!"));
            }
        }).start();
    }

    public void close() throws IOException {
        if (socket != null) socket.close();
    }
}