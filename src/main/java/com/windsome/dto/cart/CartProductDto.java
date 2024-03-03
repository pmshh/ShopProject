package com.windsome.dto.cart;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartProductDto {

    @NotNull(message = "상품 아이디는 필수 입력 값 입니다.")
    private Long productId;

    @Min(value = 1, message = "최소 1개 이상 담아주세요.")
    private int count;
}