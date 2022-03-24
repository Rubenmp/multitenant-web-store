package com.mws.backend.account.interfaces.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCreateDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
