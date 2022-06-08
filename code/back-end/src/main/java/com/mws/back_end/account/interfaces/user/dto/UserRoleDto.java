package com.mws.back_end.account.interfaces.user.dto;

import com.mws.back_end.account.model.entity.UserRole;

import static com.mws.back_end.framework.utils.ExceptionUtils.requireNotNull;

public enum UserRoleDto {
    SUPER, ADMIN, USER;

    public static UserRoleDto of(final UserRole role) {
        requireNotNull(role, "Role must be not null");
        return UserRoleDto.valueOf(role.toString());
    }
}

