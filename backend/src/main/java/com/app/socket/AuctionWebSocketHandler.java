package com.app.socket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.app.socket.dto.MessageType;
import com.app.socket.dto.Request;
import com.app.socket.dto.Response;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuctionWebSocketHandler extends TextWebSocketHandler {
    
    private final SocketRequestProcessor requestProcessor;

    private final ConcurrentHashMap<String, Set<WebSocketSession>> auctionRooms = new ConcurrentHashMap<>();

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        sessions.add(session);

        System.out.println("[WS} New client connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message){

        String json = message.getPayload();

        if (json == null || json.isBlank()){

            sendToSession(session, Response.error("Empty request"));

            return;
        }

        if (!json.trim().startsWith("{")){

            sendToSession(session, Response.error("Invalid request format"));

            return;
        }

        try {

            Request request = Request.fromJson(json);

            if (request == null){
                sendToSession(session, Response.error("Invalid request JSON"));
                return;
            }

            if (request.getType() == null){
                sendToSession(session, Response.error("Message type missing"));
                return;
            }

            Response response = requestProcessor.process(request, this,session);

            if (response != null){
                sendToSession(session, response);
            }

        }catch (Exception e){

            e.printStackTrace();
            
            sendToSession(session, Response.error("Parse error: " + e.getMessage()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        sessions.remove(session);

        for (Set<WebSocketSession> room : auctionRooms.values()){

            room.remove(session);
        }

        System.out.println("[WS] Client disconnected: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception){
        System.out.println("[WS] Transport error: " + exception.getMessage());
        sessions.remove(session);
    }

    public synchronized void sendToSession(WebSocketSession session, Response response){

        if (session != null && session.isOpen() && response != null){

            try{
                session.sendMessage(new TextMessage(response.toJson()));
            } catch (IOException e){
                e.printStackTrace();

                System.out.println("[WS] Error sending to session: " + e.getMessage());
            }
        }
    }

    public void joinAuctionRoom(String auctionId, WebSocketSession session){

        if (auctionId == null || auctionId.isBlank() || session == null){
            return;
        }

        auctionRooms.putIfAbsent(auctionId, ConcurrentHashMap.newKeySet());

        auctionRooms.get(auctionId).add(session);

        System.out.println("[WS] Joined room: " + auctionId);

        broadcastViewerCount(auctionId);
    }

    public void leaveAuctionRoom(String auctionId, WebSocketSession session){

        if (auctionId == null || auctionId.isBlank() || session == null){

            return;
        }

        Set<WebSocketSession> room = auctionRooms.get(auctionId);

        if (room != null){
            room.remove(session);

            if (room.isEmpty()){
                auctionRooms.remove(auctionId);
            }

            System.out.println("[WS] Left room: " + auctionId);
        }

        broadcastViewerCount(auctionId);
    }

    public void broadcast(Response response){

        if (response == null){
            return;
        }

        for (WebSocketSession session : sessions){
            sendToSession(session, response);
        }
    }

    public void broadcastToAuctionRoom(String auctionId, Response response){

        if (auctionId == null || response == null){
            return;
        }

        Set<WebSocketSession> room = auctionRooms.get(auctionId);

        if (room == null || room.isEmpty()){
            return;
        }

        for (WebSocketSession session : sessions){

           sendToSession(session, response);
        }
    }

    public void broadcastViewerCount(String auctionId){

        Set<WebSocketSession> room = auctionRooms.get(auctionId);

        int viewerCount = room == null ? 0 : room.size();

        Response response = Response.success(MessageType.VIEWER_UPDATE, "Viewer updated", Request.mapOf("auctionId", auctionId,
                                                                                                                            "viewerCount", viewerCount
        ));

        broadcastToAuctionRoom(auctionId, response);
    }
}
