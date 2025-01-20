package com.arplanets.template.mapper;

import com.arplanets.template.domain.User;
import com.arplanets.template.dto.UserServiceAdvancedSearchDto;
import com.arplanets.template.dto.UserServiceDto;
import com.arplanets.template.enums.ResultMessage;
import com.arplanets.template.req.AuthenticationRequest;
import com.arplanets.template.req.UserCreateRequest;
import com.arplanets.template.req.UserSearchRequest;
import com.arplanets.template.req.UserUpdateRequest;
import com.arplanets.template.res.UserCreateResponse;
import com.arplanets.template.res.UserGetResponse;
import com.arplanets.template.res.UserUpdateResponse;
import org.mapstruct.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserServiceDto userToUserServiceDto(User request);
    UserGetResponse userServiceDtoToUserGetResponse(UserServiceDto request);
    List<UserGetResponse> userServiceDtoToUserGetResponse(List<UserServiceDto> request);
    UserServiceDto userCreatRequestToUserServiceDto(UserCreateRequest request);
    UserCreateResponse userServiceDtoToUserCreateResponse(UserServiceDto userServiceDto);
    UserServiceDto userUpdateRequestToUserServiceDto(UserUpdateRequest request);
    UserUpdateResponse userServiceDtoToUserUpdateResponse(UserServiceDto userServiceDto);

    default UserServiceDto userSearchRequestToUserServiceDto(UserSearchRequest request) {
        return UserServiceDto.builder()
                .email(request.getEmail())
                .advancedSearchInfo(UserServiceAdvancedSearchDto.builder()
                        .ageStart(request.getAgeStart())
                        .ageEnd(request.getAgeEnd())
                        .createdAtStart(request.getCreatedAtStart())
                        .createdAtEnd(request.getCreatedAtEnd())
                        .build())
                .build();
    }

    default UserServiceDto authenticationRequestToUserServiceDto(AuthenticationRequest request) {
        return UserServiceDto.builder()
                .email(request.getUsername())
                .password(request.getPassword())
                .build();
    }

    default User userServiceDtoToUser(UserServiceDto request, PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
    }

    default UserUpdateResponse userServiceDtoToUserUpdateResponse(UserServiceDto userServiceDto, ResultMessage message) {
        UserUpdateResponse response = userServiceDtoToUserUpdateResponse(userServiceDto);
        response.setMessage(message.getMessage());
        return response;
    }

    default UserCreateResponse userServiceDtoToUserCreateResponse(UserServiceDto userServiceDto, ResultMessage message) {
        UserCreateResponse response = userServiceDtoToUserCreateResponse(userServiceDto);
        response.setMessage(message.getMessage());
        return response;
    }
}
