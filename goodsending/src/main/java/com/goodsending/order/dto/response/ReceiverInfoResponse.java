package com.goodsending.order.dto.response;

import com.goodsending.order.entity.Order;
import lombok.Builder;

/**
 * @author : jieun
 * @Date : 2024. 08. 02.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
@Builder
public record ReceiverInfoResponse(
    String receiverName,

    String receiverCellNumber,

    String receiverAddress

) {
    public static ReceiverInfoResponse from(Order order) {
        return ReceiverInfoResponse.builder()
            .receiverName(order.getReceiverName())
            .receiverCellNumber(order.getReceiverCellNumber())
            .receiverAddress(order.getReceiverAddress())
            .build();
    }
}
