package com.example.accountservice.fakeData;

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

        User userOne = new User();
        userOne.setEmail("JordanRad@gmail.com");
        userOne.setPassword(encoder.encode("admin"));
        userOne.setRole("ROLE_ADMIN");

        repository.save(userOne);
        System.out.println(String.format("User with email: %s has just been added.",userOne.getEmail()));

        User userTwo = new User();
        userTwo.setEmail("ivan@gmail.com");
        userTwo.setPassword(encoder.encode("admin"));

        repository.save(userTwo);
        System.out.println(String.format("User with email: %s has just been added.",userTwo.getEmail()));

        User userThree = new User();
        userThree.setEmail("b.u@gmail.com");
        userThree.setPassword(encoder.encode("admin"));

        repository.save(userThree);
        System.out.println(String.format("User with email: %s has just been added.",userThree.getEmail()));


    }
}

