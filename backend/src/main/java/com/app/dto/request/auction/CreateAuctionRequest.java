package com.app.dto.request.auction;

import java.util.List;

import com.app.dto.request.item.CreateItemRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAuctionRequest {

    // ====== AUCTION ======
    @NotBlank (message = "Tiêu đề không được trống")
    @Size (max = 255, message = "Tiêu đề tối đa 255 ký tự")
    private String title;

    @NotNull
    private CreateItemRequest item;

    @NotEmpty (message = "Phải có ít nhất 1 ảnh")
    @Size (max = 10, message = "Tối đa 10 ảnh")
    private List<@NotBlank(message = "URL ảnh không được để trống")
                @Pattern(regexp = "^(http|https)://.*$",
                        message = "URL ảnh không hợp lệ") String> imageUrls;

}
