package com.goodsending.order.repository;

import static com.goodsending.bid.entity.QBid.bid;
import static com.goodsending.order.entity.QOrder.order;

import com.goodsending.order.entity.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderQueryDslRepositoryImpl implements  OrderQueryDslRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<Order> findOrderWithBidById(Long orderId) {
    return Optional.ofNullable(queryFactory
        .selectFrom(order)
        .leftJoin(order.bid, bid).fetchJoin()
        .where(order.id.eq(orderId))
        .fetchOne());
  }
}
