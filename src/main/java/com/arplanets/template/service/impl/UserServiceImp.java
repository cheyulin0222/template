package com.arplanets.template.service.impl;

import com.arplanets.template.dto.UserServiceDto;
import com.arplanets.template.exception.TemplateApiException;
import com.arplanets.template.exception.enums.UserErrorCode;
import com.arplanets.template.log.Logger;
import com.arplanets.template.mapper.UserMapper;
import com.arplanets.template.repository.UserRepository;
import com.arplanets.template.domain.User;
import com.arplanets.template.req.UserSearchRequest;
import com.arplanets.template.res.UserCreateResponse;
import com.arplanets.template.res.UserGetResponse;
import com.arplanets.template.service.UserService;
import com.arplanets.template.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.arplanets.template.exception.ErrorType.BUSINESS;
import static com.arplanets.template.log.enums.UserAcionType.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserSpecification userSpecification;

    @Override
    public UserServiceDto getUser(String id) {
        Optional<User> option = userRepository.findById(id);
        
        if (option.isEmpty()) {
            Logger.error("帳號不存在", GET_USER, BUSINESS);
            throw new TemplateApiException(UserErrorCode._002, GET_USER);
        }

        User user = option.get();

        return userMapper.userToUserServiceDto(user);
    }

    @Override
    public UserServiceDto createUser(UserServiceDto request) {

        String email = request.getEmail();

        Optional<User> optional = userRepository.findByEmail(email);

        if (optional.isPresent()) {
            Logger.error("帳號已存在", CREATE_USER, BUSINESS);
            throw new TemplateApiException(UserErrorCode._001, CREATE_USER);
        }

        User user = userMapper.userServiceDtoToUser(request, passwordEncoder);

        User returnUser = userRepository.save(user);

        return userMapper.userToUserServiceDto(returnUser);
    }

    @Override
    public UserServiceDto updateUser(UserServiceDto request) {
        String email = request.getEmail();

        Optional<User> optional = userRepository.findByEmail(email);

        if (optional.isEmpty()) {
            Logger.error("帳號不存在", UPDATE_USER, BUSINESS);
            throw new TemplateApiException(UserErrorCode._002, UPDATE_USER);
        }

        User user = userMapper.userServiceDtoToUser(request, passwordEncoder);

        User returnUser = userRepository.save(user);

        return userMapper.userToUserServiceDto(returnUser);
    }



    @Override
    public List<UserServiceDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserServiceDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserServiceDto> getAllPaged(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest)
                .map(userMapper::userToUserServiceDto);
    }

    @Override
    public Page<UserServiceDto> searchUser(UserServiceDto request, PageRequest pageRequest) {
        Specification<User> spec = userSpecification.createSpecification(request);
        return userRepository.findAll(spec, pageRequest)
                .map(userMapper::userToUserServiceDto);
    }

    @Override
    public void deleteUser(String id) {
        Optional<User> option = userRepository.findById(id);

        if (option.isEmpty()) {
            Logger.error("帳號不存在", DELETE_USER, BUSINESS);
            throw new TemplateApiException(UserErrorCode._002, DELETE_USER);
        }

        userRepository.deleteById(id);
    }

}
