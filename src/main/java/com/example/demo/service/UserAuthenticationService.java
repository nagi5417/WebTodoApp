package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.entity.User;

@Service
public class UserAuthenticationService implements UserDetailsService {
    @Autowired
    private UserDao userDao;

    public UserAuthenticationService() {
        System.out.println("UserAuthenticationService constructor called!");
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> findResult = userDao.findByEmail(email);
        User user = findResult.orElseThrow(() -> new UsernameNotFoundException("user not found: " + email) );

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .authorities("ROLE_" + user.getRole())
            .build();
    }
}