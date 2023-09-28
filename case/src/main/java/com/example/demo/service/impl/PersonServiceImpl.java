package com.example.demo.service.impl;

import com.example.demo.dto.Response;
import com.example.demo.entities.Person;
import com.example.demo.exception.PersonNotFoundException;
import com.example.demo.repositories.PersonRepository;
import com.example.demo.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    private PersonRepository repository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceImpl.class);

    public Mono<ResponseEntity<Response>> createPerson(Person person) {
        String methodName = "CREATE PERSON";
        return Mono
                .just(person)
                .map(p -> {
                    Response response = new Response("User created with success", repository.save(person));
                    LOGGER.info("[{}] - User created with success: {}", methodName, response.getData());
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .doOnError(error -> LOGGER.error("Unable to create person {}", error.getMessage()))
                .onErrorReturn(ResponseEntity.badRequest().body(new Response("Unable to create person", null)));
    }


    @Override
    public Mono<ResponseEntity<Response>> updatePerson(Person updatedPerson) {
        String methodName = "UPDATE PERSON";
        return Mono
                .just(updatedPerson)
                .map(up -> {
                    Optional<Person> old = repository.findByEmail(up.getEmail());
                    if (old.isPresent()) {
                        old.get().setAddress(up.getAddress());
                        old.get().setNome(up.getNome());
                        old.get().setDataDeNascimento(LocalDate.now());
                        Response response = new Response("User Updated", repository.save(old.get()));
                        LOGGER.info("[{}] - User updated with success: {}", methodName, response.getData());
                        return ResponseEntity.ok().body(response);
                    } else {
                        throw new PersonNotFoundException("Person not found");
                    }
                })
                .doOnError(error -> LOGGER.error("Unable to update person {}", error.getMessage()))
                .onErrorReturn(ResponseEntity.badRequest().body(new Response("Unable to update person", null)));
    }

    @Override
    public Mono<ResponseEntity<Response>> findPerson(String email) {
        String methodName = "FIND PERSON";
        return Mono.just(email)
                .map(p -> {
                    Optional<Person> personFromRepository = repository.findByEmail(email);
                    if (personFromRepository.isPresent()) {
                        Response response = new Response("User found", personFromRepository.get());
                        LOGGER.info("[{}] - User found with success: {}", methodName, response.getData());
                        return ResponseEntity.ok(response);
                    } else {
                        throw new PersonNotFoundException("Person not found");
                    }
                })
                .doOnError(error -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response("User not found", null)));
    }

    @Override
    public Flux<Person> findAll() {
        return Mono.just(repository.findAll()).flatMapMany(Flux::fromIterable);
    }

}
