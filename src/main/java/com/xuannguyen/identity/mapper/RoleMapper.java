package com.xuannguyen.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.xuannguyen.identity.dto.request.RoleRequest;
import com.xuannguyen.identity.dto.response.RoleResponse;
import com.xuannguyen.identity.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
