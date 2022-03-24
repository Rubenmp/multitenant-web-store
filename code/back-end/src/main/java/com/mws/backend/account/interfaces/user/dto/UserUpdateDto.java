package com.mws.backend.account.interfaces.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateDto extends UserCreateDto {
    private Long id;
}
