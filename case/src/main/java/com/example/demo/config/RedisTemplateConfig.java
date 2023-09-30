package com.example.demo.config;

import com.example.demo.entities.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveKeyCommands;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveStringCommands;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisTemplateConfig {
    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private Integer port;
    @Bean
    public RedisTemplate<String, Person> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Person> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new PersonRedisSerializer());
        return template;
    }


    @Bean
    public RedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }


}
