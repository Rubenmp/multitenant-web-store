package com.mws.back_end.account.interfaces.user.dto;

import com.mws.back_end.account.model.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserDto implements Serializable {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    public static UserDto toDto(User userEntity) {
        final UserDto user = new UserDto();
        user.setId(userEntity.getId());
        user.setEmail(userEntity.getEmail());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());

        return user;
    }
}
