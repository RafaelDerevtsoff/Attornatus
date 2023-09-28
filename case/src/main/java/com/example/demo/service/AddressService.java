package com.example.demo.service;

import com.example.demo.dto.Response;
import com.example.demo.entities.Address;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AddressService {

    Mono<Response> createAddress(String email, Address address);
    Flux<Address> findAllAddress(String email);
    Mono<Address> findPrincipalAddress(String email);
}
