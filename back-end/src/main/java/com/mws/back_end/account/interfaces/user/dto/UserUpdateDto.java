package com.mws.back_end.account.interfaces.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateDto extends UserCreationDto {
    private Long id;
}
