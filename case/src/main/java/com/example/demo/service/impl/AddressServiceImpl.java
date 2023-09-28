package com.example.demo.service.impl;

import com.example.demo.dto.Response;
import com.example.demo.entities.Address;
import com.example.demo.entities.Person;
import com.example.demo.exception.PersonNotFoundException;
import com.example.demo.exception.PrincipalAddressNotFoundException;
import com.example.demo.repositories.PersonRepository;
import com.example.demo.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private PersonRepository repository;
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceImpl.class);

    @Override
    public Mono<Response> createAddress(String email, Address address) {
        String methodName = "CREATE ADDRESS";
        return Mono.just(email)
                .map(e -> {
                    Optional<Person> personOptional = repository.findByEmail(email);
                    if (personOptional.isPresent()) {
                        personOptional.get().getAddress().add(address);
                        repository.save(personOptional.get());
                        LOGGER.info("[{}]Address created", methodName);
                        return new Response("Address created", address);
                    } else {
                        throw new PersonNotFoundException("Unable to find person");
                    }
                })
                .doOnError(error -> LOGGER.error("[{}]Unable to find person", methodName))
                .onErrorReturn(new Response("Unable to find person", address));
    }

    @Override
    public Flux<Address> findAllAddress(String email) {
        return Mono.just(repository.findByEmail(email))
                .map(person -> {
                    if (person.isPresent()) {
                        return person.get().getAddress();
                    } else {
                        throw new PersonNotFoundException("Unable to find person");
                    }
                })
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Address> findPrincipalAddress(String email) {
        return Mono.just(repository.findByEmail(email))
                .map(optionalPerson -> {
                    if (optionalPerson.isPresent()){
                        return optionalPerson.get().getAddress().stream().filter(Address::getIsPrincipal).findFirst().get();
                    }else {
                        throw new PrincipalAddressNotFoundException("Unable to find Principal Address");
                    }
                })
                .doOnError(error -> LOGGER.error("No principal address available"));
    }
}
