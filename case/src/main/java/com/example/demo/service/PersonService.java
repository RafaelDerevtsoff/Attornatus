package com.example.demo.service;

import com.example.demo.dto.Response;
import com.example.demo.entities.Person;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PersonService {
    Mono<ResponseEntity<Response>> createPerson(Person person);

    Mono<ResponseEntity<Response>> updatePerson(Person updatedPerson);

    Mono<ResponseEntity<Response>> findPerson(String email);

    Flux<Person> findAll();
}
