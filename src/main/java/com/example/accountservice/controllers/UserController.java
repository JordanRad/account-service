package com.example.accountservice.controllers;

import com.example.accountservice.models.AuthRequest;
import com.example.accountservice.models.User;
import com.example.accountservice.repository.UserRepository;
import com.example.accountservice.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3005")
@RestController
@RequestMapping("api/")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;
    //@Autowired
    //private RestTemplate restTemplate;

    // @Autowired
    // private WebClient.Builder webClientBuilder;
    @Autowired
    private PasswordEncoder encoder;

    public boolean isAdmin(String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userRepository.findByEmail(email);
        //System.out.println(jwtUtil.extractClaim(token, Claims::getSubject));
        return user.getRole().equals("ROLE_ADMIN") ? true : false;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader(name = "Authorization") String token) {
        List<User> users = new ArrayList<>();

        if(isAdmin(token)) {
            users = userRepository.findAll();
            return new ResponseEntity<>(users,HttpStatus.OK);
        }else{
            return new ResponseEntity<>(users,HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping("/authenticate")
    public ResponseEntity<?> generateToken(@RequestBody AuthRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception exception) {
            throw new Exception("invalid");
        }
        return new ResponseEntity<>(jwtUtil.generateToken(request.getUsername()),HttpStatus.OK);
    }

    @PostMapping("/admin")
    public ResponseEntity<?> authenticateAdmin(@RequestBody User user) {
        User currentUser = userRepository.findByEmail(user.getEmail());
        if (currentUser != null && encoder.matches(user.getPassword(), currentUser.getPassword()) && currentUser.getRole().equals("ROLE_ADMIN")) {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(currentUser.getEmail(), user.getPassword()));
            return new ResponseEntity<>(jwtUtil.generateToken(currentUser.getEmail()),HttpStatus.OK);
        } else {

            return new ResponseEntity<>("Wrong credentials",HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()) == null) {
            User newUser = new User(user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword());
            newUser.setPassword(encoder.encode(newUser.getPassword()));
            newUser.setRole("ROLE_USER");

            userRepository.save(newUser);
            return new ResponseEntity<>("Successfully registered",HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Already registered",HttpStatus.CONFLICT);
        }
    }


    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User currentUser = userRepository.findByEmail(user.getEmail());
        if (currentUser != null && encoder.matches(user.getPassword(), currentUser.getPassword())) {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(currentUser.getEmail(), user.getPassword()));

            return jwtUtil.generateToken(currentUser.getEmail());

        } else {

            return "Wrong credentials";
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody User user, @RequestHeader(name = "Authorization") String token) {
        if (this.isAdmin(token)) {
            User modifiedUser = userRepository.findById(id).orElseThrow();
            modifiedUser.setEmail(user.getEmail());
            //modifiedUser.setUsername(user.getUsername());
            modifiedUser.setFirstName(user.getFirstName());
            modifiedUser.setLastName(user.getLastName());
            modifiedUser.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(modifiedUser);
            return new ResponseEntity<>("Successfully modified user", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("You are not an admin, so you cannot modify existing data", HttpStatus.UNAUTHORIZED);
        }
    }
}
