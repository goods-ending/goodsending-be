package com.goodsending.global.redis;

import java.time.Duration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;


@RequiredArgsConstructor
public abstract class RedisRankingRepository<K, V> {

  private final String PREFIX;
  private final RedisTemplate<String, V> redisTemplate;

  public void setZSetValue(K key, V value, double score) {
    redisTemplate.opsForZSet()
        .add(PREFIX + key, value, score);
  }

  public void setExpire(K key, int expireMinutes) {
    redisTemplate.expire(PREFIX + key, Duration.ofMinutes(expireMinutes));
  }

  public void setHashValue(K key, String hashKey, V value) {
    redisTemplate.opsForHash().put(PREFIX + key, hashKey, value);
  }

  public void deleteZSetValue(K key, V value, Object score) {
    redisTemplate.opsForZSet().remove(PREFIX + key, value, score);
  }

  public void deleteHashValue(K key, V value, Object score) {
    redisTemplate.opsForHash().delete(PREFIX + key, value, score);
  }

  public String getHashValueByKey(K key, String hashKey) {
    return (String)redisTemplate.opsForHash()
        .get(PREFIX + key, hashKey);
  }

  public Set<TypedTuple<V>> getZSetTupleByKey(K key, long start, long end) {
    return redisTemplate.opsForZSet().rangeWithScores(PREFIX + key, start, end);
  }
}
