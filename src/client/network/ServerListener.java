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
import shared.Response;

import java.io.ObjectInputStream;

public class ServerListener extends Thread {
    private final ObjectInputStream input;
    private ClientMessageHandler messageHandler;

    private boolean running = true;

    public ServerListener(ObjectInputStream input, ClientMessageHandler messageHandler) {
        this.input = input;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        try {
<<<<<<< HEAD
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
=======
            while (running) {
                Object object = input.readObject();

                if (object instanceof Response) {
                    Response response = (Response) object;

                    if (messageHandler != null) {
                        messageHandler.handle(response);
                    }
                } else {
                    System.out.println("Invalid response from server");
                }
            }

        } catch (Exception e) {
            if (running) {
                System.out.println("Disconnected from server: " + e.getMessage());
            }
        }
    }

    public void stopListening() {
        running = false;
        interrupt();
    }

    public void setMessageHandler(ClientMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
>>>>>>> 9b400298307a283b9dda72788ed460b6d762656f
    }
}