package com.goodsending.order.dto.response;

import com.goodsending.product.dto.response.ProductSummaryDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

@Builder
public record OrderWithProductResponse(
    ProductSummaryDto productSummaryDto,
    OrderResponse orderResponse
) {
  @QueryProjection
  public OrderWithProductResponse {
  }
}