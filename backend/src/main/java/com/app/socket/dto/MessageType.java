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

    // Server trả danh sách phiên đấu giá cho client
    AUCTION_LIST,

    // Client yêu cầu xem chi tiết một phiên đấu giá
    GET_AUCTION_DETAIL,

    // Server trả chi tiết phiên đấu giá
    AUCTION_DETAIL,


    // Client vào xem một phiên đấu giá để nhận realtime update
    JOIN_AUCTION,

    // Client rời khỏi màn hình đấu giá realtime
    LEAVE_AUCTION,


    // Client gửi yêu cầu đặt giá
    PLACE_BID,

    // Server báo cho tất cả client rằng có giá mới
    BID_UPDATE,

    // Client yêu cầu xem lịch sử bid
    GET_BID_HISTORY,

    // Server trả lịch sử bid
    BID_HISTORY,

    // Server báo phiên đấu giá có thay đổi trạng thái
    AUCTION_STATUS_UPDATE,

    // Server báo phiên đấu giá bắt đầu
    AUCTION_STARTED,

    // Server báo phiên đấu giá đã kết thúc
    AUCTION_FINISHED,

    // Server báo phiên đấu giá bị hủy
    AUCTION_CANCELED,


    // Seller tạo sản phẩm / phiên đấu giá mới
    CREATE_AUCTION,

    // Seller sửa thông tin sản phẩm / phiên đấu giá
    UPDATE_AUCTION,

    // Seller xóa / hủy phiên đấu giá
    DELETE_AUCTION,

    // Server trả kết quả thành công
    SUCCESS,

    // Server trả kết quả thất bại / lỗi
    ERROR,

    // Server báo lỗi kết nối
    CONNECTION_ERROR
}