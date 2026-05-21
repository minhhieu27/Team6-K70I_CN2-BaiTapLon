package controller;

import model.entity.Item;
import service.ProductService;

import java.util.List;

public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Thêm sản phẩm
    public String addItem(Item item) {

        if (item == null) {
            return "Sản phẩm không hợp lệ!";
        }

        boolean success = productService.addItem(item);

        if (success) {
            return "Thêm sản phẩm thành công!";
        }

        return "Thêm sản phẩm thất bại!";
    }

    //  Xóa sản phẩm
    public String removeItem(String id) {

        boolean success = productService.removeItem(id);

        if (success) {
            return "Xóa sản phẩm thành công!";
        }

        return "Không tìm thấy sản phẩm!";
    }

    // Cập nhật sản phẩm
    public String updateItem(String id, Item newItem) {

        boolean success = productService.updateItem(id, newItem);

        if (success) {
            return "Cập nhật sản phẩm thành công!";
        }

        return "Cập nhật thất bại!";
    }

    // Tìm sản phẩm theo ID
    public Item findItemById(String id) {
        return productService.getItemById(id);
    }

    // Lấy tất cả sản phẩm
    public List<Item> getAllItems() {
        return productService.getAllItems();
    }

    // Tìm kiếm theo tên
    public List<Item> searchByName(String keyword) {
        return productService.searchByName(keyword);
    }

    // Hiển thị thông tin sản phẩm
    public String getItemInfo(Item item) {

        if (item == null) {
            return "Không tìm thấy sản phẩm!";
        }

        return "ID: " + item.getId()
                + "\nTên: " + item.getName()
                + "\nMô tả: " + item.getDescription()
                + "\nGiá khởi điểm: " + item.getStartPrice();
    }
}