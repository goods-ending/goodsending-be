package com.goodsending.bid.repository;

import com.goodsending.bid.entity.Bid;
import java.util.List;

public interface BidQueryDslRepository {
  List<Bid> findByProductId(Long productId);
}
