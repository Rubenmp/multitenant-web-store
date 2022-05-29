package com.mws.back_end.account.interfaces.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthenticationResponse {
    private String firstName;
    private String lastName;
    private String token;
}
