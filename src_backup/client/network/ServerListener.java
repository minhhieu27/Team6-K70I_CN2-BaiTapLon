package client.network;

import shared.socket.dto.Response;

import java.io.BufferedReader;

public class ServerListener implements Runnable {

    private final BufferedReader reader;
    private final SocketResponseHandler responseHandler;
    private volatile boolean running = true;

    public ServerListener(BufferedReader reader, SocketResponseHandler responseHandler) {
        this.reader = reader;
        this.responseHandler = responseHandler;
    }

    @Override
    public void run() {
        try {
            String line;

            while (running && (line = reader.readLine()) != null) {
                Response response = Response.fromJson(line);
                responseHandler.handle(response);
            }

        } catch (Exception e) {
            responseHandler.handle(Response.error("Mất kết nối server: " + e.getMessage()));
        }
    }

    public void stop() {
        running = false;
    }
}