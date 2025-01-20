package com.arplanets.template.mapper;

import com.arplanets.template.domain.User;
import com.arplanets.template.dto.service.req.UserCreateReqSO;
import com.arplanets.template.dto.service.req.UserSearchReqSO;
import com.arplanets.template.dto.service.req.UserUpdateReqSO;
import com.arplanets.template.dto.service.res.UserCreateResSO;
import com.arplanets.template.dto.service.res.UserGetResSO;
import com.arplanets.template.dto.service.res.UserUpdateResSO;
import com.arplanets.template.enums.ResultMessage;
import com.arplanets.template.dto.req.AuthenticationRequest;
import com.arplanets.template.dto.req.UserCreateRequest;
import com.arplanets.template.dto.req.UserSearchRequest;
import com.arplanets.template.dto.req.UserUpdateRequest;
import com.arplanets.template.dto.res.UserCreateResponse;
import com.arplanets.template.dto.res.UserGetResponse;
import com.arplanets.template.dto.res.UserUpdateResponse;
import org.mapstruct.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {


    // Get
    UserGetResSO userToUserGetResSO(User user);
    UserGetResponse userGetResSOToUserGetResponse(UserGetResSO userGetResSO);

    // GetAll
    List<UserGetResponse> userGetResSOToUserGetResponse(List<UserGetResSO> userGetResSOs);

    // Search
    default UserSearchReqSO userSearchRequestToUserServiceDto(UserSearchRequest request) {
        return UserSearchReqSO.builder()
                .email(request.getEmail())
                .ageStart(request.getAgeStart())
                .ageEnd(request.getAgeEnd())
                .createdAtStart(request.getCreatedAtStart())
                .createdAtEnd(request.getCreatedAtEnd())
                .build();
    }

    // Create
    UserCreateReqSO userCreateRequestToUserCreateReqSO(UserCreateRequest userCreateRequest);
    UserCreateResSO userCreateRequestToUserCreateReqSO(User user);
    UserCreateResponse userCreateResSOToUserCreateResponse(UserCreateResSO userCreateResSO);
    default User userCreateReqSOToUser(UserCreateReqSO request, PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
    }
    default UserCreateReqSO authenticationRequestToUserCreateReqSO(AuthenticationRequest request) {
        return UserCreateReqSO.builder()
                .email(request.getUsername())
                .password(request.getPassword())
                .build();
    }
    default UserCreateResponse userCreateResSOToUserCreateResponse(UserCreateResSO userCreateResSO, ResultMessage message) {
        UserCreateResponse response = userCreateResSOToUserCreateResponse(userCreateResSO);
        response.setMessage(message.getMessage());
        return response;
    }

    // Update
    UserUpdateReqSO userUpdateRequestToUserUpdateReqSO(UserUpdateRequest userUpdateRequest);
    UserUpdateResSO userToUserUpdateResSO(User user);
    UserUpdateResponse userCreateResSOToUserCreateResponse(UserUpdateResSO userUpdateResSO);
    default User userUpdateReqSOToUser(UserUpdateReqSO request, PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
    }
    default UserUpdateResponse userUpdateResSOToUserUpdateResponse(UserUpdateResSO userUpdateResSO, ResultMessage message) {
        UserUpdateResponse response = userCreateResSOToUserCreateResponse(userUpdateResSO);
        response.setMessage(message.getMessage());
        return response;
    }

}
