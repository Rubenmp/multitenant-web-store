package com.mws.back_end.account.service;

import com.mws.back_end.account.interfaces.user.dto.UserCreationDto;
import com.mws.back_end.account.model.dao.UserDao;
import com.mws.back_end.account.model.entity.User;
import com.mws.back_end.framework.TestUtils;
import com.mws.back_end.framework.exception.EntityPersistenceException;
import com.mws.back_end.framework.exception.MWSException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest extends TestUtils {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser() throws EntityPersistenceException, MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest();
        final User createdUser = new User();
        createdUser.setId(getRandomLong());
        when(userDao.create(any(User.class))).thenReturn(createdUser);

        final Long createdUserId = userService.createUser(registerRequest);

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
