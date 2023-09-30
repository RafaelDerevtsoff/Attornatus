package com.example.demo.controller;

import com.example.demo.dto.CreateAddressDTO;
import com.example.demo.dto.Response;
import com.example.demo.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("address")
public class AddressController {
    @Autowired
    AddressService service;


    @GetMapping("all")
    public Mono<Response> findAll(@RequestHeader String email){
        return service.findAllAddress(email);
    }
    @PostMapping("create-address")
    public Mono<Response> createAddress(@RequestBody CreateAddressDTO createAddressDTO){
        return service.createAddress(createAddressDTO.getEmail(), createAddressDTO.getAddress());
    }
    @GetMapping("find-principal-address")
    public Mono<Response> findPrincipalAddress(@RequestHeader String email){
        return service.findPrincipalAddress(email);
    }
}
