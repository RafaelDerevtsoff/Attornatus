package com.example.demo.service.impl;

import com.example.demo.dto.Response;
import com.example.demo.entities.Person;
import com.example.demo.exception.PersonNotFoundException;
import com.example.demo.repositories.PersonRepository;
import com.example.demo.service.PersonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
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
    @Autowired
    private RedisTemplate<String,Person> redisTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceImpl.class);

    public Mono<ResponseEntity<Response>> createPerson(Person person) {
        String methodName = "CREATE PERSON";
        ValueOperations<String, Person> ops = redisTemplate.opsForValue();
        return Mono
                .just(person)
                .map(p -> {
                    Response response = new Response("User created with success", repository.save(person));
                    ops.setIfAbsent(person.getEmail(), person);
                    LOGGER.info("[{}] - User created with success: {}", methodName, response.getData());
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .doOnError(error -> LOGGER.error("Unable to create person {}", error.getMessage()))
                .onErrorReturn(ResponseEntity.badRequest().body(new Response("Unable to create person", null)));
    }

    private Person toPerson(String value) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(value, Person.class);
    }

    @Override
    public Mono<ResponseEntity<Response>> updatePerson(Person updatedPerson) {
        ValueOperations<String, Person> ops = redisTemplate.opsForValue();
        String methodName = "UPDATE PERSON";
        return Mono.just(updatedPerson.getEmail())
                .map(email -> {
                    Person up = ops.get(email);
                    if (up != null) return updateAndSavePerson(up, ops, methodName);
                    Optional<Person> person = repository.findByEmail(email);
                    if (person.isPresent()) return updateAndSavePerson(up, ops, methodName);
                    throw new PersonNotFoundException("Person not found");
                })
//                .doOnError(error -> LOGGER.error("Unable to update person {}", error.getMessage()))
                .onErrorReturn(ResponseEntity.badRequest().body(new Response("Unable to update person", null)));
    }

    private ResponseEntity<Response> updateAndSavePerson(Person up, ValueOperations<String, Person> ops, String methodName) {
        up.setAddress(up.getAddress());
        up.setNome(up.getNome());
        up.setDataDeNascimento(up.getDataDeNascimento());
        Response response = new Response("User Updated", repository.save(up));
        ops.set(up.getEmail(), up);
        LOGGER.info("[{}] - User updated with success: {}", methodName, response.getData());
        return ResponseEntity.ok().body(response);
    }

    @Override
    public Mono<ResponseEntity<Response>> findPerson(String email) {
        ValueOperations<String, Person> ops = redisTemplate.opsForValue();
        String methodName = "FIND PERSON";
        return Mono.just(email)
                .map(e -> {
                    Person up = ops.get(email);
                    if (up != null) {
                        LOGGER.info("Redis------------------------------------------");
                        return ResponseEntity.ok(new Response("User found", up));
                    }
                    Optional<Person> personFromRepository = repository.findByEmail(email);
                    if (personFromRepository.isPresent()) {
                        return findAndUpdateCache(personFromRepository.get(), ops, methodName);
                    } else {
                        throw new PersonNotFoundException("Person not found");
                    }
                });
//                .onErrorReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response("User not found", null)));
    }

    private static ResponseEntity<Response> findAndUpdateCache(Person personFromRepository, ValueOperations<String, Person> ops, String methodName) {
        Response response = new Response("User found", personFromRepository);
        ops.set(personFromRepository.getEmail(), personFromRepository);
        LOGGER.info("[{}] - User found with success: {}", methodName, response.getData());
        return ResponseEntity.ok(response);
    }

    @Override
    public Flux<Person> findAll() {

        return Mono.just(repository.findAll()).flatMapMany(Flux::fromIterable);
    }

}
