package com.windsome.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSizeResponseDTO {

    private Long productSizeId;
    private Long productId;
    private Long sizeId;
    private String name;
}
