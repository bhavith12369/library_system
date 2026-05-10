package com.example.librarymanagement.config;

import com.example.librarymanagement.entity.User;
import com.example.librarymanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByName("admin").isEmpty()) {
            User admin = new User();
            admin.setName("admin");
            admin.setEmail("admin@lumina.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Default admin user created: admin / admin");
        }
        
        if (userRepository.findByName("student").isEmpty()) {
            User student = new User();
            student.setName("student");
            student.setEmail("student@lumina.com");
            student.setPassword(passwordEncoder.encode("student"));
            student.setRole(User.Role.STUDENT);
            userRepository.save(student);
            System.out.println("Default student user created: student / student");
        }
    }
}
