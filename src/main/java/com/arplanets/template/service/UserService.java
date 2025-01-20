package com.arplanets.template.service;

import com.arplanets.template.dto.UserServiceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserService {

    UserServiceDto getUser(String id);
    List<UserServiceDto> getAll();
    Page<UserServiceDto> getAllPaged(PageRequest pageRequest);
    Page<UserServiceDto> searchUser(UserServiceDto request, PageRequest pageRequest);
    UserServiceDto createUser(UserServiceDto request);
    UserServiceDto updateUser(UserServiceDto request);
    void deleteUser(String id);
}
