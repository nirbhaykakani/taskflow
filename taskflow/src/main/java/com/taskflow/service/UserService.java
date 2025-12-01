package com.taskflow.service;

import com.taskflow.dto.RegisterDTO;
import com.taskflow.dto.UserDTO;
import com.taskflow.entity.User;
import com.taskflow.repo.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public UserDTO registerUser(RegisterDTO registerDTO) {
        if(userRepo.existsByEmail(registerDTO.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .name(registerDTO.getName())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .build();

        User saved = userRepo.save(user);

        return UserDTO.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .build();
    }

    public Optional<User> findByEmail(String email){
        return userRepo.findByEmail(email);
    }
}
