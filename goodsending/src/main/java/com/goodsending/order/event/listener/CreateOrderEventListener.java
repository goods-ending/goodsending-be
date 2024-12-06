package com.goodsending.order.event.listener;

import com.goodsending.order.event.CreateOrderEvent;
import com.goodsending.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author : jieun
 * @Date : 2024. 12. 06.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
@Component
@RequiredArgsConstructor
public class CreateOrderEventListener {
  private final OrderService orderService;

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void onCreateOrderEvent(CreateOrderEvent event) {
    orderService.create(event.bid());
  }

}
