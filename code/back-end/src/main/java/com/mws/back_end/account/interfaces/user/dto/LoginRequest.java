package com.mws.back_end.account.interfaces.user.dto;


import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}

