package com.app.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AuctionRequest {

    @NotBlank (message = "Tiêu đề không được trống")
    private String title;

    @NotBlank (message = "Tên sản phẩm không được để trống")
    private String itemName;

    @NotNull (message = "Giá bán không được để trống")
    @Positive (message = "Giá phải lớn hơn 0")
    private BigDecimal startPrice;

    @NotBlank (message = "Mô tả không được để trống")
    private String description;
}
