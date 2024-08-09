package com.goodsending.global.redis;

import java.time.Duration;
import java.util.Set;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

public abstract class RedisRankingRepository<K, V> {

  private final String PREFIX;
  private final RedisTemplate<String, V> redisTemplate;

  public RedisRankingRepository(String PREFIX, RedisTemplate<String, V> redisTemplate) {
    this.PREFIX = PREFIX;
    this.redisTemplate = redisTemplate;
  }

  public void setZSetValue(K key, V value, double score) {
    redisTemplate.opsForZSet()
        .add(PREFIX + key, value, score);
  }

  public void setExpire(K key, int expireMinutes) {
    redisTemplate.expire(PREFIX + key, Duration.ofMinutes(expireMinutes));
  }

  public Set<TypedTuple<V>> getReverseZSetTupleByKey(K key, long start, long end) {
    return redisTemplate.opsForZSet().reverseRangeWithScores(PREFIX + key, start, end);
  }

  public boolean isExistInRedis(String key, V value) {
    Double score = redisTemplate.opsForZSet().score(PREFIX + key, value);
    return score != null;
  }

  public void increaseScore(K key, V value, int delta) {
    redisTemplate.opsForZSet().incrementScore(PREFIX + key, value, delta);
  }

}
