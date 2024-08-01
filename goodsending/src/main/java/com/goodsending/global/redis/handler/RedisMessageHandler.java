package com.goodsending.global.redis.handler;

public interface RedisMessageHandler {
  void handle(String message);
}
