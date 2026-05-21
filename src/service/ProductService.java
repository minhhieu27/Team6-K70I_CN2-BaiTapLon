package service;

import model.entity.Item;

import java.util.*;

public class ProductService {

    private final Map<String, Item> items = new HashMap<>();

 // thêm sản phẩm
    public boolean addItem(Item item) {
        if (item == null || item.getId() == null)
            return false;

        if (items.containsKey(item.getId()))
            return false;

        items.put(item.getId(), item);
        return true;
    }
    // xoá sản phẩm
    public boolean removeItem(String id) {
        if (id == null || !items.containsKey(id))
            return false;

        items.remove(id);
        return true;
    }

    //Cập nhật sản phẩm
    public boolean updateItem(String id, Item newItem) {
        if (id == null || newItem == null || !items.containsKey(id))
            return false;

        // đảm bảo không lệch id
        if (!id.equals(newItem.getId()))
            return false;

        items.put(id, newItem);
        return true;
    }

    // Lấy tất cả sản phẩm
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    // Lấy theo ID
    public Item getItemById(String id) {
        return items.get(id);
    }

    // Kiểm tra tồn tại
    public boolean exists(String id) {
        return id != null && items.containsKey(id);
    }

    // Tìm kiếm theo tên
    public List<Item> searchByName(String keyword) {
        List<Item> result = new ArrayList<>();

        if (keyword == null) return result;

        for (Item item : items.values()) {
            if (item.getName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(item);
            }
        }

        return result;
    }
}