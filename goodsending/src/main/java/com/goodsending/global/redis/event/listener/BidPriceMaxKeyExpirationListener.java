package com.goodsending.global.redis.event.listener;

import com.goodsending.bid.service.BidService;
import com.goodsending.global.redis.event.BidPriceMaxKeyExpirationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author : jieun
 * @Date : 2024. 12. 06.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
@Component
@RequiredArgsConstructor
public class BidPriceMaxKeyExpirationListener {
  private final BidService bidService;

  @EventListener
  public void handle(BidPriceMaxKeyExpirationEvent event) {
    bidService.processBidsOnExpiration(event.productId());
  }
}
