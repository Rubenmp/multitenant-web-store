package com.mws.back_end.account.interfaces.user;

import com.mws.back_end.account.interfaces.user.dto.*;
import com.mws.back_end.account.service.UserService;
import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.exception.MWSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

import static com.mws.back_end.framework.dto.WebResult.newWebResult;
import static com.mws.back_end.framework.dto.WebResult.success;
import static com.mws.back_end.framework.dto.WebResultCode.ERROR_AUTH;
import static com.mws.back_end.framework.dto.WebResultCode.ERROR_INVALID_PARAMETER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class UserInterface {
    private static final String BASE_USER_URL = "/user";
    public static final String CREATE_USER_URL = BASE_USER_URL + "/" + "create";
    public static final String UPDATE_USER_URL = BASE_USER_URL + "/" + "update";
    public static final String GET_USER_URL = BASE_USER_URL + "/" + "get";
    public static final String LOGIN_USER_URL = BASE_USER_URL + "/" + "login";

    @Autowired
    private UserService userService;

    @PostMapping(CREATE_USER_URL)
    public ResponseEntity<WebResult<Long>> createUser(@RequestBody UserCreationDto userCreationDto) {
        final Long userId;
        try {
            userId = userService.createUser(userCreationDto);
        } catch (MWSException e) {
            return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, e.getMessage()), BAD_REQUEST);
        }

        return new ResponseEntity<>(success(userId), OK);
    }

    @PutMapping(UPDATE_USER_URL)
    public ResponseEntity<WebResult<Serializable>> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        try {
            userService.updateUser(userUpdateDto);
        } catch (MWSException e) {
            return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, e.getMessage()), BAD_REQUEST);
        }

        return new ResponseEntity<>(success(), OK);
    }

    @GetMapping(GET_USER_URL)
    public ResponseEntity<WebResult<UserDto>> getUser(@RequestParam Long id) {
        UserDto user = userService.getUser(id);

        if (user != null) {
            return new ResponseEntity<>(success(user), OK);
        }

        return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, "Invalid user id"), BAD_REQUEST);
    }

    @PostMapping(LOGIN_USER_URL)
    public ResponseEntity<WebResult<UserAuthenticationResponse>> login(@RequestBody LoginRequest loginRequest) {
        UserAuthenticationResponse authResponse;
        try {
            authResponse = userService.login(loginRequest);
        } catch (MWSException e) {
            return new ResponseEntity<>(newWebResult(ERROR_AUTH, "Invalid authentication"), BAD_REQUEST);
        }

        return new ResponseEntity<>(success(authResponse), OK);
    }

}
