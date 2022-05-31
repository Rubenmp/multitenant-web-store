package com.mws.back_end.account.interfaces.user.dto;

import com.mws.back_end.account.model.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.mws.back_end.framework.utils.ExceptionUtils.requireNotNull;

@Data
@NoArgsConstructor
public class UserDto implements Serializable {
    private Long tenantId;
    private Long id;
    private UserRoleDto role;
    private String email;
    private String firstName;
    private String lastName;

    public static UserDto toDto(final User userEntity) {
        requireNotNull(userEntity, "User must be provided");
        requireNotNull(userEntity.getRole(), "User role must be provided");

        final UserDto user = new UserDto();
        user.setTenantId(userEntity.getTenantId());
        user.setId(userEntity.getId());
        user.setRole(UserRoleDto.valueOf(userEntity.getRole().toString()));
        user.setEmail(userEntity.getEmail());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());

        return user;
    }
}
