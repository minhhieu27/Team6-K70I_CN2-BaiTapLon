package client.ui;

import java.util.List;
import java.util.Map;

public interface AuctionUiListener {

    //quy định UI sẽ nhận dữ liệu socket bằng những hàm nào

    void onAuctionList(List<Map<String, Object>> auctions);

    void onAuctionDetail(Map<String, Object> auction);

    void onBidHistory(List<Map<String, Object>> bids);

    void onBidUpdate(Map<String, Object> bidUpdate);

    void onSuccess(String message, Object data);

    void onError(String message);
}