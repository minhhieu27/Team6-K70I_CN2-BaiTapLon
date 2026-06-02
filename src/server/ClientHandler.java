package server;

import shared.MessageType;
import shared.Request;
import shared.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final AuctionServer server;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    private boolean running = true;

    public ClientHandler(Socket socket, AuctionServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();

            input = new ObjectInputStream(socket.getInputStream());

            while (running) {
                Object receivedObject = input.readObject();

                if (!(receivedObject instanceof Request)) {
                    Response errorResponse = new Response(
                            false,
                            MessageType.ERROR,
                            "Invalid request format",
                            null
                    );

                    send(errorResponse);
                    continue;
                }

                Request request = (Request) receivedObject;

                Response response = RequestProcessor.process(
                        request,
                        server,
                        this
                );

                if (response != null) {
                    send(response);
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected: " + socket.getInetAddress());
        } catch (ClassNotFoundException e) {
            System.out.println("Invalid object received: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public synchronized void send(Object object) {
        try {
            if (output != null) {
                output.writeObject(object);
                output.flush();
                output.reset();
            }
        } catch (IOException e) {
            System.out.println("Send error: " + e.getMessage());
        }
    }

    public void stopClient() {
        running = false;
        closeConnection();
    }

    private void closeConnection() {
        running = false;

        server.removeClient(this);

        try {
            if (input != null) {
                input.close();
            }

            if (output != null) {
                output.close();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

        } catch (IOException e) {
            System.out.println("Close connection error: " + e.getMessage());
        }
    }
}