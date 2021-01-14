package com.example.accountservice.fakeData;

import com.example.accountservice.models.Address;
import com.example.accountservice.models.User;
import com.example.accountservice.repository.UserRepository;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CommandLineStartupRunner implements CommandLineRunner {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Initializing data.....");

        Address address = new Address();
        address.setCity("Sofia");
        address.setZipCode("1307");
        address.setCountryCode("NL");
        address.setStreet("Kozyak 47");

        User userOne = new User();
        userOne.setEmail("JordanRad@gmail.com");
        userOne.setFirstName("Jordan");
        userOne.setLastName("Radushev");
        userOne.setPassword(encoder.encode("admin"));
        userOne.setRole("ROLE_ADMIN");
        userOne.setAddress(address);

        repository.save(userOne);
        System.out.println(String.format("User with email: %s has just been added.",userOne.getEmail()));

        User userTwo = new User();
        userTwo.setFirstName("Ivan");
        userTwo.setLastName("Marinchev");
        userTwo.setEmail("ivan@gmail.com");
        userTwo.setPassword(encoder.encode("admin"));
        userTwo.setRole("ROLE_USER");
        //userTwo.setAddress(address);

        repository.save(userTwo);
        System.out.println(String.format("User with email: %s has just been added.",userTwo.getEmail()));

        User userThree = new User();
        userThree.setEmail("b.u@gmail.com");
        userThree.setLastName("Usufi");
        userThree.setFirstName("Biser");
        userThree.setPassword(encoder.encode("admin"));
        userThree.setRole("ROLE_USER");
        //userThree.setAddress(address);

        repository.save(userThree);
        System.out.println(String.format("User with email: %s has just been added.",userThree.getEmail()));


    }
}

