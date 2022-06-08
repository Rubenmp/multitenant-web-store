package com.mws.back_end.account.interfaces.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthenticationResponse implements Serializable {
    private String token;
    private String firstName;
    private String lastName;
    private UserRoleDto role;
}
