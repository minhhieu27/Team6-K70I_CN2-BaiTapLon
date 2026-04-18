package service;

import model.entity.Item;

import java.util.*;

public class ProductService {

    private Map<String, Item> items;

    public ProductService() {
        items = new HashMap<>();
    }
    public boolean addItem(Item item) {
            if (item == null)
                return false;

            if (items.containsKey(item.getId())) {
                return false; // trùng id
            }
        items.put(item.getId(), item);
        return true;
    }
    public boolean removeItem(String id) {
        if (!items.containsKey(id))
            return false;

        items.remove(id);
        return true;
    }

    public boolean updateItem(String id, Item newItem) {
        if (!items.containsKey(id))
            return false;

        items.put(id, newItem);
        return true;
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public Item findById(String id) {
        return items.get(id);
    }

    public String placeBid(String id, String user, double price) {

        Item item = items.get(id);

        if (item == null) {
            return "Không tìm thấy sản phẩm!";
        }

        if (!item.canBid()) {
            return "Phiên đấu giá không diễn ra!";
        }

        if (!item.isValidBid(price)) {
            return "Giá phải lớn hơn giá hiện tại!";
        }

        boolean success = item.placeBid(user, price);

        if (success) {
            return "Đặt giá thành công! Giá hiện tại: " + item.getCurrentPrice();
        }

        return "Đặt giá thất bại!";
    }

    public List<Item> getRunningItems() {
        List<Item> result = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.canBid()) {
                result.add(item);
            }
        }

        return result;
    }
}
