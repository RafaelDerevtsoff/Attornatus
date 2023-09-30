package com.example.demo.service.impl;

import com.example.demo.dto.Response;
import com.example.demo.entities.Person;
import com.example.demo.exception.PersonNotFoundException;
import com.example.demo.repositories.PersonRepository;
import com.example.demo.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    private PersonRepository repository;
    @Autowired
    private RedisTemplate<String, Person> redisTemplate;

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

    @Override
    public Mono<ResponseEntity<Response>> updatePerson(Person updatedPerson) {
        ValueOperations<String, Person> ops = redisTemplate.opsForValue();
        String methodName = "UPDATE PERSON";
        return Mono.just(updatedPerson.getEmail())
                .map(email -> {
                    Optional<Person> person = repository.findByEmail(email);
                    if (person.isPresent()) {
                        return updateAndSavePerson(person.get(),updatedPerson, ops, methodName);
                    }
                    throw new PersonNotFoundException("Person not found");
                })
                .doOnError(error -> LOGGER.error("Unable to update person {}", error.getMessage()))
                .onErrorReturn(ResponseEntity.badRequest().body(new Response("Unable to update person", null)));
    }


    @Override
    public Mono<ResponseEntity<Response>> findPerson(String email) {
        ValueOperations<String, Person> ops = redisTemplate.opsForValue();
        String methodName = "FIND PERSON";
        return Mono.just(email)
                .map(e -> {
                    Person up = ops.get(email);
                    if (up != null) return ResponseEntity.ok(new Response("User found", up));
                    Optional<Person> personFromRepository = repository.findByEmail(email);
                    if (personFromRepository.isPresent()) return findAndUpdateCache(personFromRepository.get(), ops, methodName);
                    throw new PersonNotFoundException("Person not found");
                });
//                .onErrorReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response("User not found", null)));
    }


    @Override
    public Flux<Person> findAll() {
        return Mono.just(repository.findAll()).flatMapMany(Flux::fromIterable);
    }
    private ResponseEntity<Response> updateAndSavePerson(Person old,Person up, ValueOperations<String, Person> ops, String methodName) {
        old.setAddress(up.getAddress());
        old.setNome(up.getNome());
        old.setDataDeNascimento(up.getDataDeNascimento());
        Response response = new Response("User Updated", repository.save(old));
        ops.set(up.getEmail(), up);
        LOGGER.info("[{}] - User updated with success: {}", methodName, response.getData());
        return ResponseEntity.ok().body(response);
    }
    private static ResponseEntity<Response> findAndUpdateCache(Person person, ValueOperations<String, Person> ops, String methodName) {
        Response response = new Response("User found", person);
        ops.set(person.getEmail(), person);
        LOGGER.info("[{}] - User found with success: {}", methodName, response.getData());
        return ResponseEntity.ok(response);
    }

}
