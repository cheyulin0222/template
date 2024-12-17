package com.arplanet.template.service;

import com.arplanet.template.repository.UserRepository;
import com.arplanet.template.domain.User;
import com.arplanet.template.res.UserCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserCreateResponse createUser(String username, String password) {


        Optional<User> optional = userRepository.findByEmail(username);

        if (optional.isPresent()) {
            throw new RuntimeException("帳號已存在");
        }

        User user = User.builder()
                .email(username)
                .password(passwordEncoder.encode(password))
                .build();

        userRepository.save(user);

        return UserCreateResponse.builder()
                .email(user.getEmail())
                .build();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
