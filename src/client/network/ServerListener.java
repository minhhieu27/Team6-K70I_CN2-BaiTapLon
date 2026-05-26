package client.network;

import shared.socket.dto.Response;

import java.io.BufferedReader;

public class ServerListener extends Thread {

    private final BufferedReader input;
    private final SocketResponseHandler responseHandler;

    private boolean running = true;

    public ServerListener(BufferedReader input, SocketResponseHandler responseHandler) {
        this.input = input;
        this.responseHandler = responseHandler;
    }

    @Override
    public void run() {
        try {
            String json;

            while (running && (json = input.readLine()) != null) {
                Response response = Response.fromJson(json);

                if (responseHandler != null) {
                    responseHandler.handle(response);
                }
            }

            if (running && responseHandler != null) {
                responseHandler.handle(
                        Response.connectionError("Server closed the connection")
                );
            }

        } catch (Exception e) {
            if (running && responseHandler != null) {
                responseHandler.handle(
                        Response.connectionError("Disconnected from server: " + e.getMessage())
                );
            }
        }
    }

    public void stopListening() {
        running = false;
        interrupt();
    }
}