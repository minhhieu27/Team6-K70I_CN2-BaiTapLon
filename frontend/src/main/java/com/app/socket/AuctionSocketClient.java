package com.app.socket;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.app.model.Request;
import com.google.gson.Gson;

public class AuctionSocketClient {
    
    private WebSocketClient client;

    private final Gson gson = new Gson();

    private Consumer<String> onMessageCallback;

    public void connect(String token, Consumer<String> onMessage){
        
        this.onMessageCallback = onMessage;

        try{
            URI uri = new URI("wss://team6-k70i-cn2-baitaplon.onrender.com/ws/auction?token=" + token);

            client = new WebSocketClient(uri) {
                
                @Override
                public void onOpen(ServerHandshake handshake){
                    System.out.println("WebSocket connected");
                }

                @Override
                public void onMessage(String message){
                    System.out.println("WS: " + message);

                    if (onMessageCallback != null){
                        onMessageCallback.accept(message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote){
                    System.out.println("WebSocket closed");
                }

                @Override
                public void onError(Exception ex){

                    ex.printStackTrace();
                }
            };

            client.connect();
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }

   public void joinRoom(String auctionId){

        Request request = new Request("JOIN_AUCTION", Map.of("auctionId", auctionId));

        client.send(gson.toJson(request));
   }

   public void leaveRoom(String auctionId){
        
        Request request = new Request("LEAVE_AUCTION", Map.of("auctionId", auctionId));

        client.send(gson.toJson(request));
   }

   public void placeBid(String auctionId, String userId, BigDecimal price){

        Request request = new Request("PLACE_BID", Map.of("auctionId", auctionId,
                                                            "userId", userId,
                                                            "price", price));

        client.send(gson.toJson(request));
   }

   public void close(){
    if (client != null){
        client.close();
    }
   }
}
