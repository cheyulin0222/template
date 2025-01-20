package com.arplanets.template.service.impl;

import com.arplanets.template.dto.service.req.UserCreateReqSO;
import com.arplanets.template.dto.service.req.UserSearchReqSO;
import com.arplanets.template.dto.service.req.UserUpdateReqSO;
import com.arplanets.template.dto.service.res.UserCreateResSO;
import com.arplanets.template.dto.service.res.UserGetResSO;
import com.arplanets.template.dto.service.res.UserUpdateResSO;
import com.arplanets.template.exception.TemplateApiException;
import com.arplanets.template.exception.enums.UserErrorCode;
import com.arplanets.template.log.Logger;
import com.arplanets.template.mapper.UserMapper;
import com.arplanets.template.repository.UserRepository;
import com.arplanets.template.domain.User;
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
    public UserGetResSO getUser(String id) {
        Optional<User> option = userRepository.findById(id);
        
        if (option.isEmpty()) {
            Logger.error("帳號不存在", GET_USER, BUSINESS);
            throw new TemplateApiException(UserErrorCode._002, GET_USER);
        }

        User user = option.get();

        return userMapper.userToUserGetResSO(user);
    }

    @Override
    public UserCreateResSO createUser(UserCreateReqSO userCreateReqSO) {

        String email = userCreateReqSO.getEmail();

        Optional<User> optional = userRepository.findByEmail(email);

        if (optional.isPresent()) {
            Logger.error("帳號已存在", CREATE_USER, BUSINESS);
            throw new TemplateApiException(UserErrorCode._001, CREATE_USER);
        }

        User user = userMapper.userCreateReqSOToUser(userCreateReqSO, passwordEncoder);

        User returnUser = userRepository.save(user);

        return userMapper.userCreateRequestToUserCreateReqSO(returnUser);
    }

    @Override
    public UserUpdateResSO updateUser(UserUpdateReqSO userUpdateReqSO) {
        String email = userUpdateReqSO.getEmail();

        Optional<User> optional = userRepository.findByEmail(email);

        if (optional.isEmpty()) {
            Logger.error("帳號不存在", UPDATE_USER, BUSINESS);
            throw new TemplateApiException(UserErrorCode._002, UPDATE_USER);
        }

        User user = userMapper.userUpdateReqSOToUser(userUpdateReqSO, passwordEncoder);

        User returnUser = userRepository.save(user);

        return userMapper.userToUserUpdateResSO(returnUser);
    }



    @Override
    public List<UserGetResSO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserGetResSO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserGetResSO> getAllPaged(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest)
                .map(userMapper::userToUserGetResSO);
    }

    @Override
    public Page<UserGetResSO> searchUser(UserSearchReqSO request, PageRequest pageRequest) {
        Specification<User> spec = userSpecification.createSpecification(request);
        return userRepository.findAll(spec, pageRequest)
                .map(userMapper::userToUserGetResSO);
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
