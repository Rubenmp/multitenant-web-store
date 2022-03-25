package com.mws.backend.account.interfaces.user;

import com.mws.backend.account.interfaces.user.dto.UserCreationDto;
import com.mws.backend.account.interfaces.user.dto.UserUpdateDto;
import com.mws.backend.account.service.UserService;
import com.mws.backend.framework.dto.WebResult;
import com.mws.backend.framework.exception.MWSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mws.backend.framework.dto.WebResult.success;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class UserInterface {
    private static final String BASE_USER_URL = "/user";
    public static final String CREATE_USER_URL = BASE_USER_URL + "/" + "create";
    public static final String UPDATE_USER_URL = BASE_USER_URL + "/" + "update";

    @Autowired
    private UserService userService;

    @PostMapping(CREATE_USER_URL)
    public ResponseEntity<WebResult<Long>> createUser(@RequestBody UserCreationDto userCreationDto) {
        final Long userId;
        try {
            userId = userService.createUser(userCreationDto);
        } catch (MWSException e) {
            return new ResponseEntity<>(BAD_REQUEST);
        }

        return new ResponseEntity<>(success(userId), OK);
    }

    @PutMapping(UPDATE_USER_URL)
    public ResponseEntity<Void> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        try {
            userService.updateUser(userUpdateDto);
        } catch (MWSException e) {
            return new ResponseEntity<>(BAD_REQUEST);
        }

        return new ResponseEntity<>(OK);
    }

}
