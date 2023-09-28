package com.example.demo.service;

import com.example.demo.entities.Address;
import com.example.demo.entities.Person;
import com.example.demo.repositories.AddressRepository;
import com.example.demo.repositories.PersonRepository;
import com.example.demo.service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashSet;

@SpringBootTest
public class AddressServiceTest {
    @Autowired
    AddressService addressService;
    @Autowired
    PersonRepository repository;
    @Autowired
    AddressRepository addressRepository;
    private final Person mockPerson = new Person("Rafael", LocalDate.now(), "emailTest", new HashSet<>());
    private final Address mockAddress = new Address("logradouro", 0L, 258L, "Valinhos", false, mockPerson);

    @Test
    public void createAddress() {
        repository.save(mockPerson);
        addressService.createAddress(mockPerson.getEmail(), mockAddress).subscribe();
        System.out.printf("tets");
    }
    @Test
    public void findAllAddress() {
        repository.save(mockPerson);
        addressService.createAddress(mockPerson.getEmail(), mockAddress).subscribe();
        addressService.findAllAddress(mockPerson.getEmail());
        System.out.printf("tets");
    }
    @Test
    public void findPrincipalAddress() {
        repository.save(mockPerson);
        addressService.createAddress(mockPerson.getEmail(), mockAddress).subscribe();
        Address otherAddress = new Address("test",2L,2L,"Campinas",true,mockPerson);
        addressService.createAddress(mockPerson.getEmail(), otherAddress).subscribe();
        addressService.findPrincipalAddress(mockPerson.getEmail());
        System.out.printf("tets");
    }
}
