package com.example.demo.config;

import com.example.demo.entities.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
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
//    @Bean
//    public RedisTemplate<String, Person> RedisTemplate(ReactiveRedisConnectionFactory factory) {
//        StringRedisSerializer keySerializer = new StringRedisSerializer();
//        Jackson2JsonRedisSerializer<Person> valueSerializer = new Jackson2JsonRedisSerializer<>(Person.class);
//        RedisSerializationContext.RedisSerializationContextBuilder<String, Person> builder = RedisSerializationContext.newSerializationContext(keySerializer);
//        RedisSerializationContext<String, Person> context = builder.value(valueSerializer).build();
//        return new RedisTemplate<>().;
//    }
    @Bean
    public RedisTemplate<String, Person> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Person> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new PersonRedisSerializer());
        // Add some specific configuration here. Key serializers, etc.
        return template;
    }
    @Bean
    public ReactiveKeyCommands keyCommands(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return reactiveRedisConnectionFactory.getReactiveConnection().keyCommands();
    }

    @Bean
    public RedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory("0.0.0.0", 6379);
    }

    @Bean
    public ReactiveStringCommands stringCommands(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return reactiveRedisConnectionFactory.getReactiveConnection().stringCommands();
    }
}
