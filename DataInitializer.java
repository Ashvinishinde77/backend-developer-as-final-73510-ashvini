package com.booking.config;

import com.booking.entity.Role;
import com.booking.entity.User;
import com.booking.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepository
                    .findByUsername("admin")
                    .isEmpty()) {

                User admin = new User(
                        "admin",
                        passwordEncoder
                                .encode("Admin@123"),
                        Role.ADMIN
                );

                userRepository.save(admin);
            }

            if (userRepository
                    .findByUsername("user")
                    .isEmpty()) {

                User user = new User(
                        "user",
                        passwordEncoder
                                .encode("User@123"),
                        Role.USER
                );

                userRepository.save(user);
            }
        };
    }
}
