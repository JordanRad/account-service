package com.example.accountservice.controllers;

import com.example.accountservice.models.AuthRequest;
import com.example.accountservice.models.User;
import com.example.accountservice.repository.UserRepository;
import com.example.accountservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.net.http.HttpHeaders;
import java.util.List;

@CrossOrigin(origins = "*")
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

    public boolean isAdmin(String token){
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);
        return user.getRole().equals("ROLE_ADMIN")? true: false;
    }

    @GetMapping("/index")
    public String index() {
        return "Hello";
    }


    @GetMapping("/users")
    public List<User> getAllUsers(@RequestHeader(name = "Authorization") String token){
        return userRepository.findAll();
    }


    @PostMapping("/authenticate")
    public String generateToken(@RequestBody AuthRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception exception) {
            throw new Exception("invalid");
        }
        return jwtUtil.generateToken(request.getUsername());
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()) == null) {
            User newUser = new User(user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
            newUser.setPassword(encoder.encode(newUser.getPassword()));
            newUser.setRole("ROLE_USER");

            userRepository.save(newUser);
            return "Succesfully registered";
        } else {
            return "Already registered";
        }
    }


    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User currentUser = userRepository.findByEmail(user.getEmail());
        if (currentUser != null && encoder.matches(user.getPassword(), currentUser.getPassword())) {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(currentUser.getUsername(), user.getPassword()));
            return jwtUtil.generateToken(currentUser.getUsername());

        } else {

            return "Wrong credentials";
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable long id,@RequestBody User user,@RequestHeader(name ="Authorization") String token){
        if(this.isAdmin(token)){
            User modifiedUser = userRepository.findById(id).orElseThrow();
            modifiedUser.setEmail(user.getEmail());
            modifiedUser.setUsername(user.getUsername());
            modifiedUser.setFirstName(user.getFirstName());
            modifiedUser.setLastName(user.getLastName());
            modifiedUser.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(modifiedUser);
            return new ResponseEntity<>("Successfully modified user", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("You are not an admin, so you cannot modify existing data", HttpStatus.CONFLICT);
        }
    }
}
