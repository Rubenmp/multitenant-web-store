package com.mws.back_end.account.interfaces.user.dto;


import lombok.Data;

@Data
public class LoginRequest {
    private Long tenantId;
    private String email;
    private String password;
}

