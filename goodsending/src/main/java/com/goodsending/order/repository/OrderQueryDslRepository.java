package com.goodsending.order.repository;

import com.goodsending.order.entity.Order;
import java.util.Optional;

public interface OrderQueryDslRepository {
  Optional<Order> findOrderWithBidById(Long orderId);
}
