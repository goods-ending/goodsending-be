package com.goodsending.global.redis.handler;

import com.goodsending.bid.entity.Bid;
import com.goodsending.bid.repository.BidQueryDslRepository;
import com.goodsending.bid.type.BidStatus;
import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.entity.Member;
import com.goodsending.order.entity.Order;
import com.goodsending.order.repository.OrderRepository;
import com.goodsending.product.entity.Product;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("bidPriceMaxKeyExpirationHandler")
@RequiredArgsConstructor
public class BidPriceMaxKeyExpirationHandler implements RedisMessageHandler {
  private final BidQueryDslRepository bidQueryDslRepository;
  private final OrderRepository orderRepository;

  @Override
  @Transactional
  public void handle(String message) {
    long productId = Long.parseLong(message.split(":")[1]);
    List<Bid> bids = bidQueryDslRepository.findByProductId(productId);
    if(bids.isEmpty()) throw CustomException.from(ExceptionCode.BID_NOT_FOUND);

    setProduct(bids);
    handlerBids(bids);
  }

  private void setProduct(List<Bid> bids) {
    Product product = bids.get(0).getProduct();
    product.setBiddingCount((long) bids.size());
    product.setDynamicEndDateTime(LocalDateTime.now());
  }

  private void handlerBids(List<Bid> bids) {
    // 입찰금이 제일 큰 사람은 낙찰 성공해서 주문이 진행된다.
    Bid winningBid = bids.get(0);
    saveOrderByBid(winningBid);
    winningBid.setStatus(BidStatus.SUCCESSFUL);

    // 나머지 입찰자들에 대한 환불 처리
    Map<Member, Integer> cashRefunds = new HashMap<>();
    Map<Member, Integer> pointRefunds = new HashMap<>();

    for (int i = 1; i < bids.size(); i++) {
      Bid bid = bids.get(i);
      bid.setStatus(BidStatus.FAILED);
      
      int price = (bid.getPrice() != null) ? bid.getPrice() : 0;
      int usePoint = (bid.getUsePoint() != null) ? bid.getUsePoint() : 0;
      int refundCash = price - usePoint;

      Member member = bid.getMember();
      cashRefunds.merge(member, refundCash, Integer::sum);
      pointRefunds.merge(member, usePoint, Integer::sum);
    }

    // Cash와 Point를 한 번에 업데이트
    for (Map.Entry<Member, Integer> entry : cashRefunds.entrySet()) {
      Member member = entry.getKey();
      member.addCash(entry.getValue());
    }

    for (Map.Entry<Member, Integer> entry : pointRefunds.entrySet()) {
      Member member = entry.getKey();
      member.addPoint(entry.getValue());
    }
  }

  private Order saveOrderByBid(Bid bid) {
    return orderRepository.save(Order.from(bid));
  }
}
