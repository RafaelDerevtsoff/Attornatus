package com.example.demo.service;

import com.example.demo.dto.Response;
import com.example.demo.entities.Person;
import com.example.demo.repositories.PersonRepository;
import com.example.demo.service.PersonService;
import com.example.demo.service.impl.PersonServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

@SpringBootTest
public class PersonServiceTest {
    @Autowired
    PersonService service;
    @Autowired
    PersonRepository repository;

    private final Person mockPerson = new Person("Rafael", LocalDate.now(),"emailTest", new HashSet<>());
    private final Response expectedResponse = new Response("User created with success", mockPerson);

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
        service.createPerson(mockPerson).subscribe();
        service.updatePerson(new Person("Leo",LocalDate.now(),"emailTest",new HashSet<>())).subscribe();
    }
}
