package client.network;

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
    }
}