package com.app.socket;

import com.app.common.money.Money;
import com.app.service.bid.BidQuerryService;
import com.app.service.bid.BidService;
import com.app.dto.response.auction.AuctionResponse;
import com.app.dto.response.bid.BidResponse;
import com.app.service.auction.AuctionManagementService;
import com.app.service.auction.AuctionQuerryService;
import com.app.socket.dto.MessageType;
import com.app.socket.dto.Request;
import com.app.socket.dto.Response;
import com.app.entity.auction.AuctionEntity;
import com.app.repository.AuctionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.app.dto.response.security.LoginResponse;
import com.app.dto.response.user.UserResponse;
import com.app.service.user.UserService;
import com.app.dto.request.auction.CreateAuctionRequest;
import com.app.dto.request.item.CreateArtAuctionRequest;
import com.app.dto.request.item.CreateItemRequest;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SocketRequestProcessor {

    private final AuctionManagementService auctionManagementService;

    private final BidService bidService;

    private final BidQuerryService bidQuerryService;

    private final UserService userService;

    private final AuctionQuerryService auctionQuerryService;

    private final AuctionRepository auctionRepository;

    public Response process(
            Request request,
            AuctionSocketServer server,
            ClientConnectionHandler clientHandler
    ) {
        if (request == null || request.getType() == null) {
            return Response.error("Invalid request");
        }

        try {
            switch (request.getType()) {
                case GET_AUCTIONS:
                    return handleGetAuctions(request);

                case GET_AUCTION_DETAIL:
                    return handleGetAuctionDetail(request);

                case CREATE_AUCTION:
                    return handleCreateAuction(request);

                case JOIN_AUCTION:
                    return handleJoinAuction(request, server, clientHandler);

                case LEAVE_AUCTION:
                    return handleLeaveAuction(request, server, clientHandler);

                case PLACE_BID:
                    return handlePlaceBid(request, server);

                case LOGOUT:
                    return handleLogout();

                case LOGIN:
                    return handleLogin(request);

                case REGISTER:
                    return handleRegister(request);

                case GET_BID_HISTORY:
                    return handleGetBidHistory(request);

                default:
                    return Response.error("Unsupported message type: " + request.getType());
            }

        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
    private Response handleGetBidHistory(Request request) {
        String auctionId = request.getString("auctionId");

        Integer pageValue = request.getInt("page");
        Integer sizeValue = request.getInt("size");

        int page = pageValue != null ? pageValue : 0;
        int size = sizeValue != null ? sizeValue : 20;

        if (auctionId == null || auctionId.trim().isEmpty()) {
            return Response.error("Auction id is missing");
        }

        Page<BidResponse> history = bidQuerryService.getAuctionBidHistory(auctionId, page, size);

        return Response.success(
                MessageType.BID_HISTORY,
                "Get bid history success",
                history
        );
    }

    private Response handleLogin(Request request) {
        String identifier = request.getString("identifier");
        String password = request.getString("password");

        if (identifier == null || identifier.trim().isEmpty()) {
            return Response.error("Identifier is missing");
        }

        if (password == null || password.trim().isEmpty()) {
            return Response.error("Password is missing");
        }

        try {
            LoginResponse loginResponse = userService.login(identifier, password);

            return Response.success(
                    MessageType.SUCCESS,
                    "Login success",
                    loginResponse
            );

        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    private Response handleRegister(Request request) {
        String username = request.getString("username");
        String email = request.getString("email");
        String phone = request.getString("phone");
        String password = request.getString("password");

        if (username == null || username.trim().isEmpty()) {
            return Response.error("Username is missing");
        }

        if (email == null || email.trim().isEmpty()) {
            return Response.error("Email is missing");
        }

        if (phone == null || phone.trim().isEmpty()) {
            return Response.error("Phone is missing");
        }

        if (password == null || password.trim().isEmpty()) {
            return Response.error("Password is missing");
        }

        UserResponse userResponse = userService.register(
                username,
                email,
                phone,
                password
        );

        return Response.success(
                MessageType.SUCCESS,
                "Register success",
                userResponse
        );
    }

    private Response handleGetAuctions(Request request) {
        Integer pageValue = request.getInt("page");
        Integer sizeValue = request.getInt("size");

        int page = pageValue != null ? pageValue : 0;
        int size = sizeValue != null ? sizeValue : 20;

        Page<AuctionEntity> auctions = auctionRepository.findAll(PageRequest.of(page, size));

        List<Map<String, Object>> content = auctions.getContent()
                .stream()
                .map(auction -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("auctionId", auction.getAuctionId());
                    map.put("title", auction.getTitle());
                    map.put("currentPrice", auction.getCurrentPrice().getValue());
                    map.put("status", auction.getStatus().toString());
                    return map;
                })
                .toList();

        Map<String, Object> data = new HashMap<>();
        data.put("content", content);
        data.put("total", auctions.getTotalElements());
        data.put("page", page);
        data.put("size", size);

        return Response.success(
                MessageType.AUCTION_LIST,
                "Get auctions success",
                data
        );
    }

    private Response handleGetAuctionDetail(Request request) {
        String auctionId = request.getString("auctionId");

        if (auctionId == null || auctionId.trim().isEmpty()) {
            return Response.error("Auction id is missing");
        }

        AuctionEntity auction = auctionRepository.findByAuctionId(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found: " + auctionId));

        Map<String, Object> data = new HashMap<>();
        data.put("auctionId", auction.getAuctionId());
        data.put("title", auction.getTitle());
        data.put("currentPrice", auction.getCurrentPrice().getValue());
        data.put("status", auction.getStatus().toString());
        data.put("startTime", auction.getStartTime());
        data.put("endTime", auction.getEndTime());
        data.put("sellerId", auction.getSeller().getUserId());

        return Response.success(
                MessageType.AUCTION_DETAIL,
                "Get auction detail success",
                data
        );
    }

    private Response handleCreateAuction(Request request) {
        String title = request.getString("title");
        String itemName = request.getString("itemName");
        String description = request.getString("description");
        String sellerId = request.getString("sellerId");
        Double startPriceValue = request.getDouble("startPrice");

        if (title == null || title.trim().isEmpty()) {
            return Response.error("Title is missing");
        }

        if (itemName == null || itemName.trim().isEmpty()) {
            return Response.error("Item name is missing");
        }

        if (description == null || description.trim().isEmpty()) {
            return Response.error("Description is missing");
        }

        if (sellerId == null || sellerId.trim().isEmpty()) {
            return Response.error("Seller id is missing");
        }

        if (startPriceValue == null || startPriceValue <= 0) {
            return Response.error("Start price is missing or invalid");
        }

        CreateAuctionRequest createAuctionRequest = new CreateAuctionRequest();

        CreateItemRequest itemRequest = new CreateArtAuctionRequest();

        itemRequest.setItemName(itemName);
        itemRequest.setDescription(description);
        itemRequest.setStartPrice(BigDecimal.valueOf(startPriceValue));

        createAuctionRequest.setTitle(title);
        createAuctionRequest.setItem(itemRequest);

        AuctionResponse auction = auctionManagementService.createAuction(
                createAuctionRequest,
                sellerId
        );

        return Response.success(
                MessageType.SUCCESS,
                "Create auction success",
                auction
        );
    }



    private Response handleJoinAuction(
            Request request,
            AuctionSocketServer server,
            ClientConnectionHandler clientHandler
    ) {
        String auctionId = request.getString("auctionId");

        if (auctionId == null || auctionId.trim().isEmpty()) {
            return Response.error("Auction id is missing");
        }

        server.joinAuctionRoom(auctionId, clientHandler);

        return Response.success(
                MessageType.SUCCESS,
                "Joined auction room: " + auctionId,
                Request.mapOf("auctionId", auctionId)
        );
    }

    private Response handleLeaveAuction(
            Request request,
            AuctionSocketServer server,
            ClientConnectionHandler clientHandler
    ) {
        String auctionId = request.getString("auctionId");

        if (auctionId == null || auctionId.trim().isEmpty()) {
            return Response.error("Auction id is missing");
        }

        server.leaveAuctionRoom(auctionId, clientHandler);

        return Response.success(
                MessageType.SUCCESS,
                "Left auction room: " + auctionId,
                Request.mapOf("auctionId", auctionId)
        );
    }

    private Response handlePlaceBid(Request request, AuctionSocketServer server) {
        String auctionId = request.getString("auctionId");

        /*
         * Client cũ có thể gửi bidderId.
         * BidService của team dùng userId.
         * Vì vậy ưu tiên userId, nếu không có thì lấy bidderId.
         */
        String userId = request.getString("userId");

        if (userId == null) {
            userId = request.getString("bidderId");
        }

        Double priceValue = request.getDouble("price");

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

    private Response handleLogout() {
        return Response.success(
                MessageType.SUCCESS,
                "Logout success",
                null
        );
    }
    // Hàm này cố gắng tạo Money theo nhiều kiểu khác nhau.

    private Money toMoney(Double value) {
        if (value == null) {
            throw new IllegalArgumentException("Money value is null");
        }

        BigDecimal bigDecimalValue = BigDecimal.valueOf(value);

        try {
            Method ofBigDecimal = Money.class.getMethod("of", BigDecimal.class);
            return (Money) ofBigDecimal.invoke(null, bigDecimalValue);
        } catch (Exception ignored) {
        }

        try {
            Method ofDouble = Money.class.getMethod("of", double.class);
            return (Money) ofDouble.invoke(null, value);
        } catch (Exception ignored) {
        }

        try {
            Constructor<Money> constructor = Money.class.getDeclaredConstructor(BigDecimal.class);
            constructor.setAccessible(true);
            return constructor.newInstance(bigDecimalValue);
        } catch (Exception ignored) {
        }

        try {
            Constructor<Money> constructor = Money.class.getDeclaredConstructor(double.class);
            constructor.setAccessible(true);
            return constructor.newInstance(value);
        } catch (Exception ignored) {
        }

        throw new IllegalArgumentException(
                "Cannot create Money object. Please check Money.java constructor or factory method."
        );
    }
}