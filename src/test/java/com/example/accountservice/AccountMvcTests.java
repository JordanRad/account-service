package com.example.accountservice;

import com.example.accountservice.models.Address;
import com.example.accountservice.models.User;
import com.example.accountservice.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccountMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository repository;


    private String toJsonString(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }


    long test1Id;
    String testEmail;

    @BeforeEach
    public void setup() throws Exception{
        Address address = new Address();
        address.setCity("Sofia");
        address.setZipCode("1307");
        address.setCountryCode("NL");
        address.setStreet("Kozyak 47");

        User userOne = new User();
        userOne.setEmail("JordanRad@gmail.com");
        userOne.setFirstName("Jordan");
        userOne.setLastName("Radushev");
        userOne.setPassword("admin");
        userOne.setRole("ROLE_ADMIN");
        userOne.setAddress(address);

        User u = repository.save(userOne);
        test1Id = u.getId();
        testEmail = u.getEmail();

    }

    @AfterEach
    public void delete() throws Exception{
        repository.deleteAll();
    }

    @Test
    public void Should_Get_All_Users_And_Return_Status_200() throws Exception{
        this.mockMvc.perform(get("/api/users/getAll"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("Jordan")))
                .andExpect(jsonPath("$[0].lastName", is("Radushev")))
                .andExpect(jsonPath("$[0].email", is("JordanRad@gmail.com")))
                .andExpect(jsonPath("$[0].role", is("ROLE_ADMIN")));
    }
    @Test
    public void Should_Get_Single_User_By_Email_And_Return_Status_200() throws Exception{
        this.mockMvc.perform(get("/api/user/getByEmail/"+testEmail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is("Jordan")))
                .andExpect(jsonPath("$.lastName", is("Radushev")))
                .andExpect(jsonPath("$.role", is("ROLE_ADMIN")));
    }
    @Test
    public void Should_Not_Get_Single_User_By_Email_And_Return_Status_404() throws Exception{
        this.mockMvc.perform(get("/api/user/getByEmail/wrong"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void Should_Create_New_User_And_Return_Status_201()throws Exception{
        User user = new User();
        user.setEmail("newuser@mail.com");
        user.setFirstName("User");
        user.setLastName("User");
        user.setPassword("user");
        user.setRole("ROLE_USER");

        this.mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(user)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void Should_Not_Create_New_User_And_Return_Status_409()throws Exception{
        User user = new User();
        user.setEmail("JordanRad@gmail.com");
        user.setFirstName("User");
        user.setLastName("User");
        user.setPassword("user");
        user.setRole("ROLE_USER");
        this.mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(user)))
                .andDo(print())
                .andExpect(status().isOk())
        .andExpect(content().string("Already registered"));
    }

    @Test
    public void Should_Not_Get_JWT_And_Return_Status_404()throws Exception{
        User user = new User();
        user.setEmail("JordanRad@gmail.com");
        user.setPassword("wrong");
        user.setRole("ROLE_ADMIN");
        this.mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(user)))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    public void Should_Update_User_And_Return_Status_200()throws Exception{
        User user = new User();
        user.setEmail("JordanRad@gmail.com");
        user.setPassword("admin");
        user.setRole("ROLE_ADMIN");
        user.setFirstName("NewName");
        Address address = new Address();
        address.setStreet("newStreet");
        user.setAddress(address);
        this.mockMvc.perform(put("/api/users/"+test1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",is("Successfully modified user")));
    }

}
