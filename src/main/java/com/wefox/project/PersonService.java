package com.wefox.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;
    public Person create(int id, String name){return personRepository.save(new Person(id, name));}
}