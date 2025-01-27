package com.goodsending.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goodsending.product.dto.response.ProductRankingDto;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @Date : 2024. 07. 27.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@Configuration
@EnableRedisRepositories
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  @Value("${spring.data.redis.password}")
  private String password;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(host);
    redisStandaloneConfiguration.setPort(port);
    redisStandaloneConfiguration.setPassword(password);
    LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
    return lettuceConnectionFactory;
  }


  /**
   * RedisMessageListenerContainer의 기본 Executor: SimpleAsyncTaskExecutor
   * <p>
   * SimpleAsyncTaskExecutor 는 사용될 때 마다, 새로운 스레드를 만들어서 run()하기 때문에
   * <p>
   * Redis에서 이벤트가 수신될 때 마다 새로운 스레드가 만들어진다.
   *
   * @return
   */
  @Bean(name = "redisMessageTaskExecutor")
  public Executor redisMessageTaskExecutor() {
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(2);
    threadPoolTaskExecutor.setMaxPoolSize(4);
    return threadPoolTaskExecutor;
  }

  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(
      RedisConnectionFactory redisConnectionFactory) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(redisConnectionFactory);
    container.setTaskExecutor(redisMessageTaskExecutor());
    return container;
  }

  @Bean(name = "integerRedisTemplate")
  public RedisTemplate<String, Integer> longRedisTemplate(
      RedisConnectionFactory redisConnectionFactory) {
    return createRedisTemplate(redisConnectionFactory, Integer.class);
  }

  @Bean
  public RedisTemplate<String, ProductRankingDto> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, ProductRankingDto> template = createRedisTemplate(connectionFactory, ProductRankingDto.class);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule()); // Register Java Time module for LocalDateTime

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

    return template;
  }

  private <T> RedisTemplate<String, T> createRedisTemplate(
      RedisConnectionFactory redisConnectionFactory, Class<T> type) {
    return createRedisTemplate(redisConnectionFactory, type, new ObjectMapper());
  }

  private <T> RedisTemplate<String, T> createRedisTemplate(
      RedisConnectionFactory redisConnectionFactory, Class<T> type, ObjectMapper objectMapper) {
    RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    return redisTemplate;
  }
}
