package com.xuannguyen.identity.mapper;

import org.mapstruct.Mapper;

import com.xuannguyen.identity.dto.request.ProfileCreationRequest;
import com.xuannguyen.identity.dto.request.UserCreationRequest;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);
}
