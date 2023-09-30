package com.example.demo.repository;

import com.example.demo.entities.Person;
import com.example.demo.repositories.PersonRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.HashSet;

@DataJpaTest
public class PersonRepositoryTest {
    @Autowired
    PersonRepository repository;
    private final Person mockPerson = new Person("Rafael", LocalDate.now(), "emailTest", new HashSet<>());

    @Test
    public void findEmail(){
        repository.save(mockPerson);
        Assertions.assertEquals(mockPerson,repository.findByEmail(mockPerson.getEmail()).get());
    }
}
