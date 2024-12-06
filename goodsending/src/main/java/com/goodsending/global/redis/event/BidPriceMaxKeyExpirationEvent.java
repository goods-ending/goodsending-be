package com.goodsending.global.redis.event;

/**
 * @Date : 2024. 12. 06.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
public record BidPriceMaxKeyExpirationEvent(Long productId) {
  public static BidPriceMaxKeyExpirationEvent of(final Long productId) {
    return new BidPriceMaxKeyExpirationEvent(productId);
  }
}
