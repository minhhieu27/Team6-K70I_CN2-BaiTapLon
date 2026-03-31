package auction.service;

import auction.model.Auction;

import java.util.*;

public class AuctionManager {
    
    private Map<String, Auction> map = new HashMap<>(); // Tạo bảng để lưu key-value cho các phiên đấu giá

    public void addAuction(Auction auction){ // Thêm phiên đấu giá
        map.put(auction.getItemName(), auction);
    }

    public Auction getAuction(String item){ // Lấy phiên đấu giá
        return map.get(item); 
    }
}
