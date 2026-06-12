package com.xuannguyen.identity.mapper;

import org.mapstruct.Mapper;

import com.xuannguyen.identity.dto.request.PermissionRequest;
import com.xuannguyen.identity.dto.response.PermissionResponse;
import com.xuannguyen.identity.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
