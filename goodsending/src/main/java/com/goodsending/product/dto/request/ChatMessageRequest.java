package com.goodsending.product.dto.request;

import com.goodsending.productmessage.type.MessageType;

public record ChatMessageRequest(
    Long productId,
    String message,
    MessageType type
) {

}
