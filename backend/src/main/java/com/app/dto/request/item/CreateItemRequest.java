package com.app.dto.request.item;

import java.math.BigDecimal;

import com.app.common.enums.ItemType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "itemType",
    visible = true)

@JsonSubTypes({
    @JsonSubTypes.Type(value = CreateElectronicsAuctionRequest.class,
                        name = "ELECTRONICS"),
    
    @JsonSubTypes.Type(value = CreateArtAuctionRequest.class,
                        name = "ART"),

    @JsonSubTypes.Type(value = CreateBookAuctionRequest.class,
                        name = "BOOK"),

    @JsonSubTypes.Type(value = CreateVehicleAuctionRequest.class,
                        name = "VEHICLE"),

    @JsonSubTypes.Type(value = CreateCollectibleAuctionRequest.class,
                        name = "COLLECTIBLE"),

    @JsonSubTypes.Type(value = CreateFashionAuctionRequest.class,
                        name = "FASHION"),

    @JsonSubTypes.Type(value = CreateJewelryAuctionRequest.class,
                        name = "JEWELRY")
})
public abstract class CreateItemRequest {
    
    // ====== COMMON ITEM ======

    @NotNull
    private ItemType itemType;

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
