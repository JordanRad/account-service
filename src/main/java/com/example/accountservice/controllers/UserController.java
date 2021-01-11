package com.example.accountservice.controllers;

import com.example.accountservice.models.Address;
import com.example.accountservice.models.User;
import com.example.accountservice.models.UserResponse;
import com.example.accountservice.repository.UserRepository;
import com.example.accountservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


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

    public boolean isAdmin(String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userRepository.findByEmail(email);
        return user.getRole().equals("ROLE_ADMIN") ? true : false;
    }

    @GetMapping("/users/getAll")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users;
        users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);

    }

    @GetMapping("/user/getByEmail/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        System.out.println(email);
        User user;
        user = userRepository.findByEmail(email);
        return user != null ?
                new ResponseEntity<User>(user, HttpStatus.OK) :
                new ResponseEntity<String>("No such user", HttpStatus.NOT_FOUND);

    }


//    @PostMapping("/authenticate")
//    public ResponseEntity<?> generateToken(@RequestBody AuthRequest request) throws Exception {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
//            );
//        } catch (Exception exception) {
//            throw new Exception("invalid");
//        }
//        return new ResponseEntity<>(jwtUtil.generateToken(request.getUsername()),HttpStatus.OK);
//    }

//    @PostMapping("/admin")
//    public ResponseEntity<?> authenticateAdmin(@RequestBody User user) {
//        User currentUser = userRepository.findByEmail(user.getEmail());
//        if (currentUser != null && encoder.matches(user.getPassword(), currentUser.getPassword()) && currentUser.getRole().equals("ROLE_ADMIN")) {
//            authenticationManager
//                    .authenticate(new UsernamePasswordAuthenticationToken(currentUser.getEmail(), user.getPassword()));
//            return new ResponseEntity<>(jwtUtil.generateToken(currentUser.getEmail()),HttpStatus.OK);
//        } else {
//
//            return new ResponseEntity<>("Wrong credentials",HttpStatus.UNAUTHORIZED);
//        }
//    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()) == null) {
            User newUser = new User(user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword());
            newUser.setPassword(encoder.encode(newUser.getPassword()));
            newUser.setRole("ROLE_USER");

            userRepository.save(newUser);
            return new ResponseEntity<>("Successfully registered", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Already registered", HttpStatus.OK);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User currentUser = userRepository.findByEmail(user.getEmail());
        if (currentUser != null && encoder.matches(user.getPassword(), currentUser.getPassword())) {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(currentUser.getEmail(), user.getPassword()));

            //return jwtUtil.generateToken(currentUser.getEmail());

            UserResponse response = new UserResponse(jwtUtil.generateToken(currentUser.getEmail()), currentUser.getEmail(), currentUser.getAddress(), currentUser.getFirstName(), currentUser.getLastName(), currentUser.getId());
            return new ResponseEntity(response, HttpStatus.OK);
        } else {

            return new ResponseEntity("Wrong credentials", HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/admin")
    public ResponseEntity<?> loginAdmin(@RequestBody User user) {
        User currentUser = userRepository.findByEmail(user.getEmail());
        if (currentUser != null && encoder.matches(user.getPassword(), currentUser.getPassword())) {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(currentUser.getEmail(), user.getPassword()));

            if(currentUser.getRole().equals("ROLE_ADMIN")){
            UserResponse response = new UserResponse(jwtUtil.generateToken(currentUser.getEmail()), currentUser.getEmail(), currentUser.getAddress(), currentUser.getFirstName(), currentUser.getLastName(), currentUser.getId());
            return new ResponseEntity(response, HttpStatus.OK);
            }else{

                return new ResponseEntity("Wrong credentials", HttpStatus.NOT_FOUND);
            }

        } else {

            return new ResponseEntity("Wrong credentials", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody User user) {
        User modifiedUser = userRepository.findById(id).orElseThrow();

        boolean sameEmailExists = true;
        int res = userRepository.countUserWithSameEmail(user.getEmail());
        sameEmailExists = res > 0;
        if (modifiedUser.getEmail().equals(user.getEmail())) {
            sameEmailExists = false;
        }


        if (!sameEmailExists) {
            modifiedUser.setEmail(user.getEmail());
            //modifiedUser.setUsername(user.getUsername());
            modifiedUser.setFirstName(user.getFirstName());
            modifiedUser.setLastName(user.getLastName());
            if (!user.getPassword().equals("Not changed")) {
                modifiedUser.setPassword(encoder.encode(user.getPassword()));
            }
            Address address = user.getAddress();
            System.out.println(modifiedUser.getAddress()!=null);
            System.out.println(user.getAddress().getStreet());
            if(user.getAddress()!=null) {
                if (modifiedUser.getAddress() == null || modifiedUser.getAddress().equals(address.getStreet())) {
                    modifiedUser.setAddress(address);
                }
            }
            User updatedUser = userRepository.save(modifiedUser);
            return new ResponseEntity<>("Successfully modified user", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("This email already exists in the database", HttpStatus.OK);
        }
    }
}
