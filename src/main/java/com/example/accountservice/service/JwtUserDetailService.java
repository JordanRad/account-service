package com.example.accountservice.service;

import com.example.accountservice.models.User;
import com.example.accountservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
public class JwtUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository repository;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
       User user = repository.findByEmail(email);

           return new org.springframework.security.core.userdetails.User (user.getEmail(), user.getPassword(), user.getAuthorities());

    }


}
