package com.example.demo.service;

import com.example.demo.entities.Address;
import com.example.demo.entities.Person;
import com.example.demo.repositories.PersonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
public class AddressServiceTest {
    @Autowired
    AddressService addressService;
    @MockBean
    static PersonRepository repository;
    private Person mockPerson = new Person("Rafael", LocalDate.now(), "emailTest", new HashSet<>());
    private final Address mockAddress = new Address("logradouro", 0L, 258L, "Valinhos", true);

    @Autowired
    StringRedisTemplate template;

    @BeforeEach
    public void setup() {
        Mockito.when(repository.save(mockPerson)).thenReturn(mockPerson);
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(mockPerson));
    }



    @AfterEach
    public void clean() {
        mockPerson = new Person("Rafael", LocalDate.now(), "emailTest", new HashSet<>());
    }

    @Test
    public void createAddress() {
        StepVerifier
                .create(addressService.createAddress(mockPerson.getEmail(), mockAddress))
                .expectNextMatches(response -> {
                    Address address = (Address) response.getData();
                    return address.equals(mockPerson.getAddress().stream().findFirst().get());
                })
                .verifyComplete();
    }

    @Test
    public void findAllAddress() {
        mockPerson.getAddress().add(mockAddress);
        StepVerifier
                .create(addressService.findAllAddress(mockPerson.getEmail()))
                .expectNextMatches(response -> {
                    Set<Address> addresses = (Set<Address>) response.getData();
                    return addresses.stream().findFirst().get() == mockAddress;
                })
                .verifyComplete();
    }

    @Test
    public void findPrincipalAddress() {
        mockPerson.getAddress().add(mockAddress);
        mockPerson.getAddress().add(new Address("test20", 3L, 6L, "Campinas", false));
        StepVerifier
                .create(addressService.findPrincipalAddress(mockPerson.getEmail()))
                .expectNextMatches(response -> {
                    Address address = (Address) response.getData();
                    return address.getIsPrincipal();
                })
                .verifyComplete();
    }
}
