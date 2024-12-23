package com.arplanet.template.service;

import com.arplanet.template.exception.ApiServiceException;
import com.arplanet.template.exception.enums.UserErrorCode;
import com.arplanet.template.log.Logger;
import com.arplanet.template.repository.UserRepository;
import com.arplanet.template.domain.User;
import com.arplanet.template.res.UserCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.arplanet.template.exception.ErrorType.AUTH;
import static com.arplanet.template.log.enums.UserAcionType.CREATE_USER;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger;

    public UserCreateResponse createUser(String username, String password) {


        Optional<User> optional = userRepository.findByEmail(username);

        if (optional.isPresent()) {
            logger.error("帳號已存在", CREATE_USER, AUTH);
            throw new ApiServiceException(UserErrorCode._001, CREATE_USER);
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
