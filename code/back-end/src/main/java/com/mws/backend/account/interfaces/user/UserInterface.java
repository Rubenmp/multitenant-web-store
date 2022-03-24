package com.mws.backend.account.interfaces.user;

import com.mws.backend.account.interfaces.user.dto.UserCreateDto;
import com.mws.backend.account.interfaces.user.dto.UserUpdateDto;
import com.mws.backend.account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class UserInterface {
    private static final String BASE_USER_URL = "/user";
    public static final String CREATE_USER_URL = BASE_USER_URL + "/" + "create";
    public static final String UPDATE_USER_URL = BASE_USER_URL + "/" + "update";

    @Autowired
    private UserService userService;

    @PostMapping(CREATE_USER_URL)
    public ResponseEntity<Long> createUser(@RequestBody UserCreateDto userCreateDto) {
        final Long userId = userService.createUser(userCreateDto);

        return new ResponseEntity<>(userId, OK);
    }

    @PutMapping(UPDATE_USER_URL)
    public ResponseEntity<Long> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        userService.updateUser(userUpdateDto);

        return new ResponseEntity<>(OK);
    }

}
