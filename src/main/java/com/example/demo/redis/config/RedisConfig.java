package com.example.demo.redis.config;

import com.example.demo.redis.model.RedisKeyModel;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * @author: wb-yps
 * @date: 2019/11/13
 * @desc: redis 缓存配置
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 定义缓存数据 key 生成策略的bean
     * 包名+类名+方法名+所有参数
     *
     * @return
     */
    @Bean
    public KeyGenerator wiselyKeyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                RedisKeyModel redisKeyModel = new RedisKeyModel();
                redisKeyModel.setKey((String) params[0]);
                redisKeyModel.setExpire((Integer) params[1]);
                return redisKeyModel;
            }
        };
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        // 设置缓存的默认超时时间：30分钟
        redisCacheConfiguration.entryTtl(Duration.ofMinutes(30L));
        // 如果是空值，不缓存
        redisCacheConfiguration.disableCachingNullValues();
        // 设置key序列化器
        redisCacheConfiguration.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        // 设置value序列化器
        redisCacheConfiguration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        RedisCacheManager.RedisCacheManagerBuilder build = RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory));
        RedisCacheManager cacheManager = build.cacheDefaults(redisCacheConfiguration).build();
        return cacheManager;
    }

    @Bean
    public RedisConnectionFactory connectionFactory() {

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(10);
        poolConfig.setMaxWaitMillis(2000);
        poolConfig.setMinIdle(0);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(true);
        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                .usePooling().poolConfig(poolConfig).and().readTimeout(Duration.ofMillis(3000)).build();

        // 单点redis
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        // 哨兵redis
        // RedisSentinelConfiguration redisConfig = new RedisSentinelConfiguration();
        // 集群redis
        // RedisClusterConfiguration redisConfig = new RedisClusterConfiguration();
        redisConfig.setHostName("127.0.0.1");
//        redisConfig.setPassword(RedisPassword.of(redisAuth));
        redisConfig.setPort(6379);
        redisConfig.setDatabase(0);
        return new JedisConnectionFactory(redisConfig, clientConfig);
    }


}
