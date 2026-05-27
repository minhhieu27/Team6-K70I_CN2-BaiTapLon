package com.app.dto.request.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookAuctionRequest extends CreateItemRequest {
    
    @NotBlank
    private String author;

    @NotBlank
    private String publisher;

    @NotNull
    private Integer publishYear;
}
