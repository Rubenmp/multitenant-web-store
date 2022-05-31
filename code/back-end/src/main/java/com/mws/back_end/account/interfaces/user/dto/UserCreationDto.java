package com.mws.back_end.account.interfaces.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCreationDto {
    private Long tenantId;
    private UserRoleDto role;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
