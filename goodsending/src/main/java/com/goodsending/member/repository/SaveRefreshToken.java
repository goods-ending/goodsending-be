package com.goodsending.member.repository;

import com.goodsending.global.redis.RedisRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SaveRefreshToken extends RedisRepository<String, String> {
  private static final String PREFIX = "refresh_token:";

  public SaveRefreshToken(RedisTemplate<String, String> redisTemplate) {
    super(PREFIX, redisTemplate);
  }
}

