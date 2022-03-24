package com.mws.backend.account.interfaces.user;

import com.mws.backend.account.interfaces.user.dto.UserCreateDto;
import com.mws.backend.account.interfaces.user.dto.UserUpdateDto;
import com.mws.backend.account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class UserInterface {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @PostMapping("/create-user")
    public ResponseEntity<Long> createUser(@RequestBody UserCreateDto userCreateDto) {
        final Long userId = userService.createUser(userCreateDto);

        return new ResponseEntity<>(userId, OK);
    }

    @PutMapping("/update-user")
    public ResponseEntity<Long> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        userService.updateUser(userUpdateDto);

        return new ResponseEntity<>(OK);
    }

}
