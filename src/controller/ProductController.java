package controller;

import model.entity.Item;

public class ProductController {

    private Item item;

    public ProductController(Item item) {
        this.item = item;
    }

    public String getStatusMessage() {
        switch (item.getStatus()) {
            case RUNNING:
                return "Phiên đấu giá đang diễn ra";

            case COMING_SOON:
                return "Phiên đấu giá sắp diễn ra";

            case NOT_STARTED:
                return "Phiên đấu giá chưa diễn ra";

            case FINISHED:
                return "Phiên đấu giá đã kết thúc";
        }
        return "";
    }

    public String handleBid(String user, double price) {

        if (!item.canBid()) {
            return "Không thể đấu giá lúc này!";
        }

        if (!item.isValidBid(price)) {
            return "Đặt giá không hợp lệ!";
        }

        item.placeBid(user, price);

        return "Đặt giá thành công! Giá hiện tại: " + item.getCurrentPrice();
    }

    public String getItemInfo() {
        return "Tên: " + item.getName()
                + "\nGiá hiện tại: " + item.getCurrentPrice()
                + "\nNgười dẫn đầu: " + item.getCurrentWinner();
    }
}