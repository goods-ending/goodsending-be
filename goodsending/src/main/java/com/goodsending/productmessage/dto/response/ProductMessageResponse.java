package com.goodsending.productmessage.dto.response;

import com.goodsending.productmessage.type.MessageType;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

/**
 * @Date : 2024. 08. 08.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
public record ProductMessageResponse(
    Long id,
    Long memberId,
    Long productId,
    String message,
    MessageType type,
    LocalDateTime createdDateTime
) {

  @QueryProjection
  public ProductMessageResponse {
  }
}
