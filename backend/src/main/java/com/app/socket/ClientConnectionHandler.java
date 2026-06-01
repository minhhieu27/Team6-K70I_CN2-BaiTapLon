package com.app.socket;

import com.app.socket.dto.MessageType;
import com.app.socket.dto.Request;
import com.app.socket.dto.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnectionHandler extends Thread {

    private final Socket socket;
    private final AuctionSocketServer server;
    private final SocketRequestProcessor requestProcessor;

    private BufferedReader input;
    private PrintWriter output;

    private boolean running = true;

    public ClientConnectionHandler(
            Socket socket,
            AuctionSocketServer server,
            SocketRequestProcessor requestProcessor
    ) {
        this.socket = socket;
        this.server = server;
        this.requestProcessor = requestProcessor;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            String json;

            while (running && (json = input.readLine()) != null) {

                if (json.isBlank()) continue;

                if (!json.trim().startsWith("{")) {
                    send(Response.error("Invalid request format"));

                    continue;
                }
                
                Request request = Request.fromJson(json);

                if (request == null || request.getType() == null) {
                    send(Response.error("Invalid request JSON"));
                    continue;
                }

                Response response = requestProcessor.process(request, server, this);

                if (response != null) {
                    send(response);
                }

                if (request.getType() == MessageType.LOGOUT) {
                    running = false;
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Socket client disconnected: " + socket.getInetAddress());
        } catch (Exception e) {
            System.out.println("Client handler error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public synchronized void send(Response response) {
        if (output != null && response != null) {
            output.println(response.toJson());
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
            System.out.println("Close socket error: " + e.getMessage());
        }
    }
}