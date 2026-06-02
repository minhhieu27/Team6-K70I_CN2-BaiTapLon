package server;

import shared.MessageType;
import shared.Request;
import shared.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestProcessor {

    public static Response process(
            Request request,
            AuctionServer server,
            ClientHandler clientHandler
    ) {
        if (request == null) {
            return new Response(false, MessageType.ERROR, "Request is null", null);
        }

        if (request.getType() == null) {
            return new Response(false, MessageType.ERROR, "Request type is null", null);
        }

        switch (request.getType()) {
            case LOGIN:
                return handleLogin(request);

            case REGISTER:
                return handleRegister(request);

            case LOGOUT:
                return handleLogout(clientHandler);

            case GET_AUCTIONS:
                return handleGetAuctions();

            case GET_AUCTION_DETAIL:
                return handleGetAuctionDetail(request);

            case JOIN_AUCTION:
                return handleJoinAuction(request, server, clientHandler);

            case LEAVE_AUCTION:
                return handleLeaveAuction(request, server, clientHandler);

            case PLACE_BID:
                return handlePlaceBid(request, server);

            case GET_BID_HISTORY:
                return handleGetBidHistory(request);

            case CREATE_AUCTION:
                return handleCreateAuction(request);

            case UPDATE_AUCTION:
                return handleUpdateAuction(request);

            case DELETE_AUCTION:
                return handleDeleteAuction(request);

            default:
                return new Response(
                        false,
                        MessageType.ERROR,
                        "Unsupported message type: " + request.getType(),
                        null
                );
        }
    }

    private static Response handleLogin(Request request) {
        Map<String, Object> data = request.getData();

        if (data == null) {
            return new Response(false, MessageType.ERROR, "Login data is empty", null);
        }

        Object username = data.get("username");
        Object password = data.get("password");

        if (username == null || password == null) {
            return new Response(
                    false,
                    MessageType.ERROR,
                    "Username or password is missing",
                    null
            );
        }


        Map<String, Object> result = new HashMap<>();
        result.put("username", username.toString());
        result.put("role", "BIDDER");

        return new Response(
                true,
                MessageType.SUCCESS,
                "Login success",
                result
        );
    }

    private static Response handleRegister(Request request) {
        Map<String, Object> data = request.getData();

        if (data == null) {
            return new Response(false, MessageType.ERROR, "Register data is empty", null);
        }

        Object username = data.get("username");
        Object password = data.get("password");

        if (username == null || password == null) {
            return new Response(
                    false,
                    MessageType.ERROR,
                    "Username or password is missing",
                    null
            );
        }

        return new Response(
                true,
                MessageType.SUCCESS,
                "Register success",
                data
        );
    }

    private static Response handleLogout(ClientHandler clientHandler) {
        clientHandler.stopClient();

        return new Response(
                true,
                MessageType.SUCCESS,
                "Logout success",
                null
        );
    }

    private static Response handleGetAuctions() {

        List<Map<String, Object>> auctions = new ArrayList<>();

        Map<String, Object> auction1 = new HashMap<>();
        auction1.put("auctionId", "A001");
        auction1.put("itemName", "Laptop Gaming");
        auction1.put("currentPrice", 5000000);
        auction1.put("status", "OPEN");

        Map<String, Object> auction2 = new HashMap<>();
        auction2.put("auctionId", "A002");
        auction2.put("itemName", "Điện thoại iPhone");
        auction2.put("currentPrice", 8000000);
        auction2.put("status", "OPEN");

        auctions.add(auction1);
        auctions.add(auction2);

        return new Response(
                true,
                MessageType.AUCTION_LIST,
                "Get auctions success",
                auctions
        );
    }

    private static Response handleGetAuctionDetail(Request request) {
        Map<String, Object> data = request.getData();

        if (data == null || data.get("auctionId") == null) {
            return new Response(
                    false,
                    MessageType.ERROR,
                    "Auction id is missing",
                    null
            );
        }

        String auctionId = data.get("auctionId").toString();


        Map<String, Object> auctionDetail = new HashMap<>();
        auctionDetail.put("auctionId", auctionId);
        auctionDetail.put("itemName", "Demo Item");
        auctionDetail.put("description", "Demo auction detail");
        auctionDetail.put("startPrice", 1000000);
        auctionDetail.put("currentPrice", 1500000);
        auctionDetail.put("highestBidder", "U001");
        auctionDetail.put("status", "OPEN");

        return new Response(
                true,
                MessageType.AUCTION_DETAIL,
                "Get auction detail success",
                auctionDetail
        );
    }

    private static Response handleJoinAuction(
            Request request,
            AuctionServer server,
            ClientHandler clientHandler
    ) {
        Map<String, Object> data = request.getData();

        if (data == null || data.get("auctionId") == null) {
            return new Response(
                    false,
                    MessageType.ERROR,
                    "Auction id is missing",
                    null
            );
        }

        String auctionId = data.get("auctionId").toString();

        server.joinAuctionRoom(auctionId, clientHandler);

        return new Response(
                true,
                MessageType.SUCCESS,
                "Joined auction room: " + auctionId,
                auctionId
        );
    }

    private static Response handleLeaveAuction(
            Request request,
            AuctionServer server,
            ClientHandler clientHandler
    ) {
        Map<String, Object> data = request.getData();

        if (data == null || data.get("auctionId") == null) {
            return new Response(
                    false,
                    MessageType.ERROR,
                    "Auction id is missing",
                    null
            );
        }

        String auctionId = data.get("auctionId").toString();

        server.leaveAuctionRoom(auctionId, clientHandler);

        return new Response(
                true,
                MessageType.SUCCESS,
                "Left auction room: " + auctionId,
                auctionId
        );
    }

    private static Response handlePlaceBid(Request request, AuctionServer server) {
        Map<String, Object> data = request.getData();

        if (data == null) {
            return new Response(false, MessageType.ERROR, "Bid data is empty", null);
        }

        Object auctionIdObject = data.get("auctionId");
        Object bidderIdObject = data.get("bidderId");
        Object priceObject = data.get("price");

        if (auctionIdObject == null || bidderIdObject == null || priceObject == null) {
            return new Response(
                    false,
                    MessageType.ERROR,
                    "Bid information is missing",
                    null
            );
        }

        String auctionId = auctionIdObject.toString();
        String bidderId = bidderIdObject.toString();

        double price;

        try {
            price = Double.parseDouble(priceObject.toString());
        } catch (NumberFormatException e) {
            return new Response(
                    false,
                    MessageType.ERROR,
                    "Invalid bid price",
                    null
            );
        }

        if (price <= 0) {
            return new Response(
                    false,
                    MessageType.ERROR,
                    "Bid price must be greater than 0",
                    null
            );
        }


        Map<String, Object> bidUpdateData = new HashMap<>();
        bidUpdateData.put("auctionId", auctionId);
        bidUpdateData.put("bidderId", bidderId);
        bidUpdateData.put("price", price);
        bidUpdateData.put("timestamp", System.currentTimeMillis());

        Response bidUpdateResponse = new Response(
                true,
                MessageType.BID_UPDATE,
                "New bid update",
                bidUpdateData
        );

        server.broadcastToAuctionRoom(auctionId, bidUpdateResponse);

        return new Response(
                true,
                MessageType.SUCCESS,
                "Bid placed successfully",
                bidUpdateData
        );
    }

    private static Response handleGetBidHistory(Request request) {
        Map<String, Object> data = request.getData();

        if (data == null || data.get("auctionId") == null) {
            return new Response(
                    false,
                    MessageType.ERROR,
                    "Auction id is missing",
                    null
            );
        }

        String auctionId = data.get("auctionId").toString();


        List<Map<String, Object>> history = new ArrayList<>();

        Map<String, Object> bid1 = new HashMap<>();
        bid1.put("auctionId", auctionId);
        bid1.put("bidderId", "U001");
        bid1.put("price", 1200000);
        bid1.put("timestamp", System.currentTimeMillis());

        Map<String, Object> bid2 = new HashMap<>();
        bid2.put("auctionId", auctionId);
        bid2.put("bidderId", "U002");
        bid2.put("price", 1500000);
        bid2.put("timestamp", System.currentTimeMillis());

        history.add(bid1);
        history.add(bid2);

        return new Response(
                true,
                MessageType.BID_HISTORY,
                "Get bid history success",
                history
        );
    }

    private static Response handleCreateAuction(Request request) {
        Map<String, Object> data = request.getData();

        if (data == null) {
            return new Response(
                    false,
                    MessageType.ERROR,
                    "Auction data is empty",
                    null
            );
        }

        return new Response(
                true,
                MessageType.SUCCESS,
                "Create auction success",
                data
        );
    }

    private static Response handleUpdateAuction(Request request) {
        Map<String, Object> data = request.getData();

        if (data == null || data.get("auctionId") == null) {
            return new Response(
                    false,
                    MessageType.ERROR,
                    "Auction id is missing",
                    null
            );
        }

        String auctionId = data.get("auctionId").toString();

        Response updateResponse = new Response(
                true,
                MessageType.AUCTION_STATUS_UPDATE,
                "Auction updated",
                data
        );

        // Nếu client đang xem auction này thì gửi realtime update.

        serverBroadcastAuctionUpdateLater();

        return new Response(
                true,
                MessageType.SUCCESS,
                "Update auction success: " + auctionId,
                data
        );
    }

    private static Response handleDeleteAuction(Request request) {
        Map<String, Object> data = request.getData();

        if (data == null || data.get("auctionId") == null) {
            return new Response(
                    false,
                    MessageType.ERROR,
                    "Auction id is missing",
                    null
            );
        }

        String auctionId = data.get("auctionId").toString();

        return new Response(
                true,
                MessageType.SUCCESS,
                "Delete auction success",
                auctionId
        );
    }

    private static void serverBroadcastAuctionUpdateLater() {

    }
}