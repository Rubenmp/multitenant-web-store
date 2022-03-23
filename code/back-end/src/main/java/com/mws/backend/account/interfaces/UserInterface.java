package com.mws.backend.account.interfaces;

import com.mws.backend.account.model.entity.User;
import com.mws.backend.account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInterface {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/create-user")
    public String createUser() {
        final User user = userService.createUser("example@email.com");

        if (user == null) {
            return "User was not created";
        }
        return "User created with id: " + user.getId();
    }

}
