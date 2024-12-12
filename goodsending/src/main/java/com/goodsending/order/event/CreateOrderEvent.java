package com.goodsending.order.event;

import com.goodsending.bid.entity.Bid;

/**
 * @author : jieun
 * @Date : 2024. 12. 06.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
public record CreateOrderEvent(Bid bid) {
  public static CreateOrderEvent of(Bid bid) {
    return new CreateOrderEvent(bid);
  }
}
