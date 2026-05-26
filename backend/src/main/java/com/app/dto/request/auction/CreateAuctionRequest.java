package com.app.dto.request.auction;

import java.math.BigDecimal;
import java.util.List;

import com.app.common.enums.ItemType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CreateAuctionRequest {

    // ====== AUCTION ======
    @NotBlank (message = "Tiêu đề không được trống")
    @Size (max = 255, message = "Tiêu đề tối đa 255 ký tự")
    private String title;

    @NotNull (message = "Vui lòng chọn kiểu hàng")
    private ItemType itemType;

    @NotEmpty (message = "Phải có ít nhất 1 ảnh")
    @Size (max = 10, message = "Tối đa 10 ảnh")
    private List<@NotBlank(message = "URL ảnh không được để trống")
                @Pattern(regexp = "^(http|https)://.*$",
                        message = "URL ảnh không hợp lệ") String> imageUrls;

    // ====== COMMON ITEM ======
    @NotBlank (message = "Tên sản phẩm không được để trống")
    @Size (message = "Tên sản phẩm tối đa 255 ký tự")
    private String itemName;

    @NotBlank (message = "Mô tả không được để trống")
    @Size (max = 2000, message = "Mô tả tối đa 2000 ký tự")
    private String description;

    @NotNull (message = "Giá bán không được để trống")
    @Positive (message = "Giá bán phải lớn hơn 0")
    private BigDecimal startPrice;
}
