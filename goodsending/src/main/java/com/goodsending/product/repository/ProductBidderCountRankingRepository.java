package com.goodsending.product.repository;

import com.goodsending.global.redis.RedisRankingRepository;
import com.goodsending.product.dto.response.ProductRankingDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductBidderCountRankingRepository extends RedisRankingRepository<String, ProductRankingDto> {

  private static final String PREFIX = "PRODUCT_BIDDING_COUNT:";

  public ProductBidderCountRankingRepository(RedisTemplate<String, ProductRankingDto> redisTemplate) {
    super(PREFIX, redisTemplate);
  }

}
