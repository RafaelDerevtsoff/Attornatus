package com.example.demo.controller;

import com.example.demo.dto.Response;
import com.example.demo.entities.Person;
import com.example.demo.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping
public class PersonController {
    @Autowired
    PersonService personService;

    @PostMapping("create-user")
    public Mono<ResponseEntity<Response>> createUser(@RequestBody Person newPerson) {
        return personService.createPerson(newPerson);
    }

    @PutMapping("update-user")
    public Mono<ResponseEntity<Response>> updatePerson(@RequestBody Person updatedPerson) {
        return personService.updatePerson(updatedPerson);
    }

    @GetMapping("find-user")
    public Mono<ResponseEntity<Response>> findPerson(@RequestHeader String email) {
        return personService.findPerson(email);
    }

    @GetMapping("all")
    public Flux<Person> findAll(){
        return personService.findAll();
    }
}
