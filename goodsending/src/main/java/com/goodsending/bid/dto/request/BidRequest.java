package com.goodsending.bid.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * @Date : 2024. 07. 25.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
public record BidRequest(

    @NotNull(message = "입찰가는 필수 입력 항목입니다.")
    @Positive(message = "입찰가는 0원 이상이어야 합니다.")
    Integer bidPrice,

    @Positive(message = "사용 포인트는 0원 이상이어야 합니다.")
    Integer usePoint,

    @NotNull(message = "상품 id는 필수 입력 항목입니다.")
    Long productId

) {

}