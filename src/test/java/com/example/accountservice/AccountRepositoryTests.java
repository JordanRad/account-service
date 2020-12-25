package com.example.accountservice;

import com.example.accountservice.models.Address;
import com.example.accountservice.models.User;
import com.example.accountservice.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class AccountRepositoryTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;


    private User userOne;

    @BeforeEach
    public void setup(){
        Address address = new Address();
        address.setCity("Sofia");
        address.setZipCode("1307");
        address.setCountryCode("NL");
        address.setStreet("Kozyak 47");

        userOne = new User();
        userOne.setEmail("test@test.com");
        userOne.setFirstName("TestFirstName");
        userOne.setLastName("TestLastName");
        userOne.setPassword("hello");
        userOne.setRole("ROLE_ADMIN");
        userOne.setAddress(address);

        entityManager.persist(userOne);
    }

    @Test
    public void Should_Get_A_Single_User_By_Id() {
        Long idOne = (Long) entityManager.getId(userOne);
       User u = repository.getOne(idOne);
        assertThat(u.getEmail()).isEqualTo("test@test.com");
        assertThat(u.getAddress()).isNotNull();
        assertThat(u.getRole()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    public void Should_Get_A_Single_User_By_Email() {
        User u = repository.findByEmail("test@test.com");
        assertThat(u).isNotNull();
    }

    @Test
    public void Should_Count_Users_With_Same_Email() {
        int moreThanZero = repository.countUserWithSameEmail(userOne.getEmail());
        int zero = repository.countUserWithSameEmail("mail");
        assertThat(moreThanZero).isEqualTo(1);
        assertThat(zero).isEqualTo(0);
    }

    @Test
    public void Should_Create_User() {
        User newUser = new User();
        newUser.setEmail("new@test.com");
        newUser.setFirstName("NewFirstName");
        newUser.setLastName("NewLastName");

        User saved = repository.save(newUser);

        assertThat(saved.getEmail()).isEqualTo("new@test.com");
        assertThat(saved.getFirstName()).isEqualTo("NewFirstName");
        assertThat(saved.getLastName()).isEqualTo("NewLastName");
    }

    @Test
    public void Should_Update_User() {
        Long idOne = (Long) entityManager.getId(userOne);
        User u = repository.getOne(idOne);
        u.setEmail("updated@test.com");
        u.setFirstName("AnotherFirstName");
        u.setLastName("AnotherLastName");

        User updated = repository.save(u);

        assertThat(updated.getEmail()).isEqualTo("updated@test.com");
        assertThat(updated.getFirstName()).isEqualTo("AnotherFirstName");
        assertThat(updated.getLastName()).isEqualTo("AnotherLastName");
    }


    @Test
    public void Should_Delete_User_By_Id() {
        Long idOne = (Long) entityManager.getId(userOne);
        User u = repository.getOne(idOne);
        repository.delete(u);
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            User user = repository.findById(idOne).get();
        });
    }


    @Test
    public void Should_Not_Get_NonExistent_User() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            var user = repository.findById(222L).get();
        });
    }

}
