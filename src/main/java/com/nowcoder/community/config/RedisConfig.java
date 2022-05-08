package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    //将RedisTemplate对象装配到Bean中 redisTemplate要想访问数据库，需要能创建连接，连接由连接工厂创建，注入工厂
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        //设置连接工厂
        template.setConnectionFactory(factory);

        //将数据存入数据库需要对Java数据进行数据转换
        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //设置value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        //设置hash的key的序列化方式  value本身是hash，hash又分为key value 特殊设置
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        //将设置生效
        template.afterPropertiesSet();
        return template;
    }
}
