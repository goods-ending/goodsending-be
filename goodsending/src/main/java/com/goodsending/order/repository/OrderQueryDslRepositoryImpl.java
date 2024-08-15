package com.goodsending.order.repository;

import static com.goodsending.bid.entity.QBid.bid;
import static com.goodsending.member.entity.QMember.member;
import static com.goodsending.order.entity.QOrder.order;
import static com.goodsending.product.entity.QProduct.product;
import static com.goodsending.product.entity.QProductImage.productImage;

import com.goodsending.order.dto.request.OrderListBySellerRequest;
import com.goodsending.order.dto.response.OrderWithProductResponse;
import com.goodsending.order.dto.response.QOrderResponse;
import com.goodsending.order.entity.Order;
import com.goodsending.product.dto.response.QProductSummaryDto;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

/**
 * @author : jieun(je-pa)
 * @Date : 2024. 08. 03.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
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

  @Override
  public Optional<Order> findOrderWithBidAndProductById(Long orderId) {
    return Optional.ofNullable(queryFactory
        .selectFrom(order)
        .innerJoin(order.bid, bid).fetchJoin()
        .innerJoin(order.bid.product, product).fetchJoin()
        .where(order.id.eq(orderId))
        .fetchOne());
  }

  @Override
  public Optional<Order> findOrderWithBidAndProductAndSellerById(Long orderId) {
    return Optional.ofNullable(queryFactory
        .selectFrom(order)
        .innerJoin(order.bid, bid).fetchJoin()
        .innerJoin(order.bid.product, product).fetchJoin()
        .innerJoin(product.member, member).fetchJoin()
        .where(order.id.eq(orderId))
        .fetchOne());
  }

  @Override
  public Slice<OrderWithProductResponse> findOrderWithProductBySeller(OrderListBySellerRequest request) {
    Pageable pageable = PageRequest.of(0, request.pageSize());

    List<OrderWithProductResponse> content = queryFactory
        .select(Projections.constructor(OrderWithProductResponse.class,
            new QProductSummaryDto(
                product.id,
                product.name,
                product.price,
                product.finalPrice,
                product.startDateTime,
                product.dynamicEndDateTime,
                product.maxEndDateTime,
                product.status,
                productImage.url
            ),
            new QOrderResponse(
                order.id,
                product.member.memberId,
                order.receiverName,
                order.receiverCellNumber,
                order.receiverAddress,
                order.deliveryDateTime,
                order.confirmedDateTime,
                order.status
            )
        ))
        .from(order)
        .innerJoin(order.bid, bid)
        .innerJoin(order.bid.product, product)
        .leftJoin(productImage)
        .on(productImage.product.id.eq(product.id)
            .and(productImage.id.eq(
                JPAExpressions.select(productImage.id.min())
                    .from(productImage)
                    .where(productImage.product.id.eq(product.id))
            ))
        )
        .where(
            ltOrderId(request.cursorId()),
            product.member.memberId.eq(request.memberId()))
        .orderBy(order.id.desc())
        .limit(request.pageSize() + 1) // Fetch one more record to check if there is a next page
        .fetch();

    boolean hasNext = content.size() > request.pageSize();
    if (hasNext) {
      content.remove(content.size() - 1); // Remove the extra record
    }

    return new SliceImpl<>(content, pageable, hasNext);
  }

  private Predicate ltOrderId(Long orderId) {
    if (orderId == null) {
      return null;
    }

    return bid.id.lt(orderId);
  }
}
