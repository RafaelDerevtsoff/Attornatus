package com.example.demo.service.impl;

import com.example.demo.dto.Response;
import com.example.demo.entities.Address;
import com.example.demo.entities.Person;
import com.example.demo.exception.HasPrincipalException;
import com.example.demo.exception.PersonNotFoundException;
import com.example.demo.exception.PrincipalAddressNotFoundException;
import com.example.demo.repositories.PersonRepository;
import com.example.demo.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private PersonRepository repository;
    private static final Logger LOGGER = LoggerFactory.getLogger(AddressServiceImpl.class);
    @Autowired
    private RedisTemplate<String, Person> redisTemplate;

    @Override
    public Mono<Response> createAddress(String email, Address address) {
        ValueOperations<String, Person> ops = redisTemplate.opsForValue();
        String methodName = "CREATE ADDRESS";
        return Mono.just(email)
                .map(e -> {
                    Optional<Person> person = repository.findByEmail(email);
                    if (person.isPresent()) {
                        validateAddress(person.get(), address,ops);
                        return new Response("Address created", address);
                    } else {
                        throw new PersonNotFoundException("Unable to find person");
                    }
                });
    }

    private void validateAddress(Person person, Address address,ValueOperations<String,Person> ops) {
        String methodName = "VALIDATE ADDRESS";
        Set<Address> addresses = person.getAddress();
        boolean isUniqueUsed = isUniqueUsed(addresses);
        if ((isUniqueUsed && address.getIsPrincipal()) ){
            throw new HasPrincipalException("");
        } else {
            addresses.add(address);
            repository.save(person);
            ops.set(person.getEmail(),person, Duration.of(15L, ChronoUnit.MINUTES));
            LOGGER.info("[{}]Address created", methodName);
        }
    }

    public boolean isUniqueUsed(Set<Address> addresses) {
        return addresses.stream().anyMatch(Address::getIsPrincipal);
    }

    @Override
    public Mono<Response> findAllAddress(String email) {
        return Mono.just(repository.findByEmail(email))
                .map(person -> {
                    if (person.isPresent()) {
                        return new Response("", person.get().getAddress());
                    } else {
                        throw new PersonNotFoundException("Unable to find person");
                    }
                })
                .onErrorReturn(new Response("Unable to find Principal Address", null));
    }

    @Override
    public Mono<Response> findPrincipalAddress(String email) {
        return Mono.just(repository.findByEmail(email))
                .map(optionalPerson -> {
                    if (optionalPerson.isPresent()) {
                        Address principalAddress = optionalPerson
                                .get()
                                .getAddress()
                                .stream().filter(Address::getIsPrincipal)
                                .findFirst()
                                .get();
                        return new Response("Principal Found", principalAddress);
                    } else {
                        throw new PrincipalAddressNotFoundException("Unable to find Principal Address");
                    }
                })
                .onErrorReturn(new Response("Unable to find Principal Address", null))
                .doOnError(error -> LOGGER.error("No principal address available"));
    }
}
