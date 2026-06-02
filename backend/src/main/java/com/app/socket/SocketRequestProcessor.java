package com.app.socket;

import com.app.common.money.Money;
import com.app.service.bid.BidService;
import com.app.dto.response.bid.BidResponse;
import com.app.socket.dto.MessageType;
import com.app.socket.dto.Request;
import com.app.socket.dto.Response;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class SocketRequestProcessor {

    private final BidService bidService;

    public Response process(
            Request request,
            AuctionWebSocketHandler server,
            WebSocketSession session
    ) {
        if (request == null || request.getType() == null) {
            return Response.error("Invalid request");
        }

        try {
            switch (request.getType()) {

                case JOIN_AUCTION:
                    return handleJoinAuction(request, server, session);

                case LEAVE_AUCTION:
                    return handleLeaveAuction(request, server, session);

                case PLACE_BID:
                    return handlePlaceBid(request, server, session);

                default:
                    return Response.error("Unsupported message type: " + request.getType());
            }

        } catch (Exception e) {
            return Response.error("Procsess error: " + e.getMessage());
        }
    }

    private Response handleJoinAuction(
            Request request,
            AuctionWebSocketHandler server,
            WebSocketSession session
    ) {
        String auctionId = request.getString("auctionId");

        if (auctionId == null || auctionId.trim().isEmpty()) {
            return Response.error("Auction id is missing");
        }

        server.joinAuctionRoom(auctionId, session);

        return Response.success(
                MessageType.SUCCESS,
                "Joined auction room: " + auctionId,
                Request.mapOf("auctionId", auctionId)
        );
    }

    private Response handleLeaveAuction(
            Request request,
            AuctionWebSocketHandler server,
            WebSocketSession session
    ) {
        String auctionId = request.getString("auctionId");

        if (auctionId == null || auctionId.trim().isEmpty()) {
            return Response.error("Auction id is missing");
        }

        server.leaveAuctionRoom(auctionId, session);

        return Response.success(
                MessageType.SUCCESS,
                "Left auction room: " + auctionId,
                Request.mapOf("auctionId", auctionId)
        );
    }

    private Response handlePlaceBid(Request request, AuctionWebSocketHandler server, WebSocketSession session) {
        String auctionId = request.getString("auctionId");

        /*
         * Client cũ có thể gửi bidderId.
         * BidService của team dùng userId.
         * Vì vậy ưu tiên userId, nếu không có thì lấy bidderId.
         */
        String userId = (String) session.getAttributes().get("userId");

        if (userId == null) {
            userId = (String) session.getAttributes().get("bidderId");
        }

        Long priceValue = request.getLong("price");

        if (auctionId == null || auctionId.trim().isEmpty()) {
            return Response.error("Auction id is missing");
        }

        if (userId == null || userId.trim().isEmpty()) {
            return Response.error("User id is missing");
        }

        if (priceValue == null || priceValue <= 0) {
            return Response.error("Bid price is missing or invalid");
        }

        Money amount = toMoney(priceValue);

        BidResponse bidResponse = bidService.placeBid(
                auctionId,
                amount,
                userId
        );

        Response bidUpdate = Response.success(
                MessageType.BID_UPDATE,
                "New bid update",
                Request.mapOf(
                        "auctionId", auctionId,
                        "bid", bidResponse
                )
        );

        server.broadcastToAuctionRoom(auctionId, bidUpdate);

        return Response.success(
                MessageType.SUCCESS,
                "Bid placed successfully",
                Request.mapOf(
                        "auctionId", auctionId,
                        "bid", bidResponse
                )
        );
    }

    private Money toMoney(Long value){

        if (value == null){
            throw new IllegalArgumentException("Money value is null");
        }

        return Money.of(value);
    }
}