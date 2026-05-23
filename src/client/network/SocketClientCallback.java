package client.network;

import shared.socket.dto.Response;

public interface SocketClientCallback {

    // Gọi khi server trả về response thành công chung
    void onSuccess(Response response);

    //Gọi khi server trả về lỗi nghiệp vụ
    void onError(Response response);

    /*
     * Gọi khi client nhận được danh sách phiên đấu giá.
     * Tương ứng MessageType.AUCTION_LIST.
     */
    void onAuctionList(Response response);

    /*
     * Gọi khi client nhận được chi tiết một phiên đấu giá.
     * Tương ứng MessageType.AUCTION_DETAIL.
     */
    void onAuctionDetail(Response response);

    /*
     * Gọi khi có cập nhật giá realtime.
     * Tương ứng MessageType.BID_UPDATE.
     */
    void onBidUpdate(Response response);

    /*
     * Gọi khi client nhận được lịch sử đặt giá.
     * Tương ứng MessageType.BID_HISTORY.
     */
    void onBidHistory(Response response);

    //Gọi khi có cập nhật trạng thái phiên đấu giá
    void onAuctionStatusUpdate(Response response);

    // Gọi khi phiên đấu giá kết thúc.
    void onAuctionFinished(Response response);

    //Gọi khi lỗi kết nối socket.
    void onConnectionError(String message);

    // Gọi khi client bị ngắt kết nối khỏi server.
    void onDisconnected();
}