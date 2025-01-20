package com.arplanets.template.service;

import com.arplanets.template.dto.service.req.UserCreateReqSO;
import com.arplanets.template.dto.service.req.UserSearchReqSO;
import com.arplanets.template.dto.service.req.UserUpdateReqSO;
import com.arplanets.template.dto.service.res.UserCreateResSO;
import com.arplanets.template.dto.service.res.UserGetResSO;
import com.arplanets.template.dto.service.res.UserUpdateResSO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserService {

    UserGetResSO getUser(String id);
    List<UserGetResSO> getAll();
    Page<UserGetResSO> getAllPaged(PageRequest pageRequest);
    Page<UserGetResSO> searchUser(UserSearchReqSO userSearchReqSO, PageRequest pageRequest);
    UserCreateResSO createUser(UserCreateReqSO userCreateReqSO);
    UserUpdateResSO updateUser(UserUpdateReqSO userUpdateReqSO);
    void deleteUser(String id);
}
