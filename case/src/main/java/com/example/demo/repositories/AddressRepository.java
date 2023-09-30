package com.example.demo.repositories;

import com.example.demo.entities.Address;
import com.example.demo.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public interface AddressRepository extends JpaRepository<Address,Long> {
}
