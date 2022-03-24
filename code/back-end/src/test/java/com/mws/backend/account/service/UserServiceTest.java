package com.mws.backend.account.service;

import com.mws.backend.account.interfaces.user.dto.UserCreationDto;
import com.mws.backend.account.model.dao.UserDao;
import com.mws.backend.account.model.entity.User;
import com.mws.backend.framework.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceTest extends TestUtils {
    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser() {
        final UserCreationDto registerRequest = getValidRegisterRequest();
        final User createdUser = new User();
        createdUser.setId(getRandomLong());
        when(userDao.create(any(User.class))).thenReturn(createdUser);

        Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }


    private UserCreationDto getValidRegisterRequest() {
        UserCreationDto registerRequest = new UserCreationDto();
        registerRequest.setFirstName("New first name");
        registerRequest.setLastName("New last name");
        registerRequest.setPassword("Password");
        registerRequest.setEmail("email@test.com");

        return registerRequest;
    }
}
