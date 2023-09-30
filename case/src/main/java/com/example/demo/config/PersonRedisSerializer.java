package com.example.demo.config;

import com.example.demo.entities.Person;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PersonRedisSerializer implements RedisSerializer<Person> {
    private static final long serialVersionUID = 1L;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public PersonRedisSerializer() {
        super();
        objectMapper.registerModule(new JavaTimeModule());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        objectMapper.setDateFormat(dateFormat);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public byte[] serialize(Person person) throws SerializationException {

        try {
            return objectMapper.writeValueAsBytes(person);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static ObjectMapper getObjectMapper() {

        return objectMapper;
    }

    @Override
    public Person deserialize(byte[] bytes) throws SerializationException {
        try {
            return objectMapper.readValue(bytes, Person.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
