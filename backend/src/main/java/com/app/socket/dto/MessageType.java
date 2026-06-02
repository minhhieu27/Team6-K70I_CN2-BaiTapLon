package com.app.socket.dto;

public enum MessageType {


    // Client gửi yêu cầu đăng nhập
    LOGIN,

    // Client gửi yêu cầu đăng ký
    REGISTER,

    // Client gửi yêu cầu đăng xuất
    LOGOUT,

    // Client yêu cầu lấy danh sách tất cả phiên đấu giá
    GET_AUCTIONS,

    // Client yêu cầu xem chi tiết một phiên đấu giá
    GET_AUCTION_DETAIL,

    // Client vào xem một phiên đấu giá để nhận realtime update
    JOIN_AUCTION,

    // Client rời khỏi màn hình đấu giá realtime
    LEAVE_AUCTION,

    // Client gửi yêu cầu đặt giá
    PLACE_BID,

    // Server báo cho tất cả client rằng có giá mới
    BID_UPDATE,

    // Seller tạo sản phẩm / phiên đấu giá mới
    CREATE_AUCTION,

    OUT_BID_NOTIFICATION,

    AUCTION_ENDED,

    // Server trả kết quả thành công
    SUCCESS,

    // Server trả kết quả thất bại / lỗi
    ERROR,

    // Server báo lỗi kết nối
    CONNECTION_ERROR,

    VIEWER_UPDATE
}