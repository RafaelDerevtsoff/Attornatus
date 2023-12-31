package com.example.demo.service;

import com.example.demo.dto.Response;
import com.example.demo.entities.Person;
import com.example.demo.repositories.PersonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.*;

@SpringBootTest
public class PersonServiceTest {
    @Autowired
    PersonService service;
    @MockBean
    PersonRepository repository;
    @MockBean
    RedisTemplate<String,Person> template;
    @MockBean
    ValueOperations<String,Person> ops;
    @MockBean
    RedisConnectionFactory factory;
    @MockBean
    RedisKeyValueAdapter adapter;

    private Person mockPerson = new Person("Rafael", LocalDate.now(), "emailTest", new HashSet<>());
    private final Response expectedResponse = new Response("User created with success", mockPerson);
    private final List<Person> persons = new ArrayList<>();


    @BeforeEach
    public void setup() {
        if (persons.isEmpty()){
            persons.add(mockPerson);
        }
        Mockito.when(repository.save(Mockito.any(Person.class))).thenReturn(mockPerson);
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(mockPerson));
        Mockito.when(repository.findAll()).thenReturn(persons);
        Mockito.when(template.opsForValue()).thenReturn(ops);
        Mockito.doNothing().when(ops).set(Mockito.anyString(),Mockito.any(Person.class));
        Mockito.when(ops.get(Mockito.anyString())).thenReturn(null);
    }
    @AfterEach
    public void clean() {
        mockPerson = new Person("Rafael", LocalDate.now(), "emailTest", new HashSet<>());
    }

    @Test
    public void createUserTest() {
        StepVerifier
                .create(service.createPerson(mockPerson))
                .expectNextMatches(responseResponseEntity -> {
                    String message = Objects.requireNonNull(responseResponseEntity.getBody()).getMessage();
                    Person person = (Person) responseResponseEntity.getBody().getData();
                    return message.equals(expectedResponse.getMessage()) && expectedResponse.getData().equals(person);
                })
                .verifyComplete();
    }

    @Test
    public void updateUser() {
        Person updatedPerson = new Person("Leo", LocalDate.now(), "emailTest", new HashSet<>());
        StepVerifier.create(service.updatePerson(updatedPerson))
                .expectNextMatches(response -> {
                    Person person = (Person) Objects.requireNonNull(response.getBody()).getData();
                    return Objects.equals(person.getNome(), updatedPerson.getNome());
                })
                .verifyComplete();
    }

    @Test
    public void findPerson() {
        StepVerifier.create(service.findPerson(mockPerson.getEmail()))
                .expectNextMatches(response -> response.getBody().getData().equals(mockPerson))
                .verifyComplete();
    }

    @Test
    public void findAll() {
        StepVerifier.create(service.findAll())
                .expectNext(mockPerson)
                .verifyComplete();
    }
}
