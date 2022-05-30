package com.mws.back_end.account.service;

import com.mws.back_end.account.interfaces.user.dto.UserCreationDto;
import com.mws.back_end.account.interfaces.user.dto.UserDto;
import com.mws.back_end.account.interfaces.user.dto.UserRoleDto;
import com.mws.back_end.account.model.dao.UserDao;
import com.mws.back_end.account.model.entity.User;
import com.mws.back_end.framework.TestUtils;
import com.mws.back_end.framework.exception.MWSException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest extends TestUtils {

    @Mock
    private UserDao userDao;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_withRoleUser_success() throws MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.USER);
        final User createdUser = new User();
        createdUser.setId(getRandomLong());
        when(userDao.create(any(User.class))).thenReturn(createdUser);

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }

    @Test
    void createUser_withRoleSuperUsingSuper_success() throws MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.SUPER);
        final User createdUser = newUser();
        when(userDao.create(any(User.class))).thenReturn(createdUser);
        when(jwtProvider.getCurrentUser()).thenReturn(userWithRole(UserRoleDto.SUPER));

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }

    @Test
    void createUser_withRoleSuperUsingAdmin_notAllowed() {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.SUPER);
        when(jwtProvider.getCurrentUser()).thenReturn(userWithRole(UserRoleDto.ADMIN));

        boolean exceptionThrown = false;
        try {
            userService.createUser(registerRequest);
        } catch (MWSException e) {
            exceptionThrown = true;
            assertEquals("Not allowed to create an user with super role.", e.getMessage(), "Exception message");
        }

        assertTrue(exceptionThrown, "Exception must be thrown");
    }

    @Test
    void createUser_withRoleSuperUsingUser_notAllowed() {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.SUPER);
        when(jwtProvider.getCurrentUser()).thenReturn(userWithRole(UserRoleDto.USER));

        boolean exceptionThrown = false;
        try {
            userService.createUser(registerRequest);
        } catch (MWSException e) {
            exceptionThrown = true;
            assertEquals("Not allowed to create an user with super role.", e.getMessage(), "Exception message");
        }

        assertTrue(exceptionThrown, "Exception must be thrown");
    }

    @Test
    void createUser_withRoleSuperWithoutAuthenticatedUser_notAllowed() {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.SUPER);
        when(jwtProvider.getCurrentUser()).thenReturn(null);

        boolean exceptionThrown = false;
        try {
            userService.createUser(registerRequest);
        } catch (MWSException e) {
            exceptionThrown = true;
            assertEquals("Not allowed to create an user with super role.", e.getMessage(), "Exception message");
        }

        assertTrue(exceptionThrown, "Exception must be thrown");
    }

    @Test
    void createUser_withRoleAdminUsingSuper_success() throws MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.ADMIN);
        final User createdUser = newUser();
        when(userDao.create(any(User.class))).thenReturn(createdUser);
        when(jwtProvider.getCurrentUser()).thenReturn(userWithRole(UserRoleDto.SUPER));

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }


    @Test
    void createUser_withRoleAdminUsingAdmin_success() throws MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.ADMIN);
        final User createdUser = newUser();
        when(userDao.create(any(User.class))).thenReturn(createdUser);
        when(jwtProvider.getCurrentUser()).thenReturn(userWithRole(UserRoleDto.ADMIN));

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }


    @Test
    void createUser_withRoleAdminUsingUser_notAllowed() {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.ADMIN);
        when(jwtProvider.getCurrentUser()).thenReturn(userWithRole(UserRoleDto.USER));

        boolean exceptionThrown = false;
        try {
            userService.createUser(registerRequest);
        } catch (MWSException e) {
            exceptionThrown = true;
            assertEquals("Not allowed to create an user with admin role.", e.getMessage(), "Exception message");
        }

        assertTrue(exceptionThrown, "Exception must be thrown");
    }

    @Test
    void createUser_withRoleAdminWithoutAuthenticatedUser_notAllowed() {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.ADMIN);
        when(jwtProvider.getCurrentUser()).thenReturn(null);

        boolean exceptionThrown = false;
        try {
            userService.createUser(registerRequest);
        } catch (MWSException e) {
            exceptionThrown = true;
            assertEquals("Not allowed to create an user with admin role.", e.getMessage(), "Exception message");
        }

        assertTrue(exceptionThrown, "Exception must be thrown");
    }

    @Test
    void createUser_withRoleUserUsingSuper_success() throws MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.USER);
        final User createdUser = newUser();
        when(userDao.create(any(User.class))).thenReturn(createdUser);
        when(jwtProvider.getCurrentUser()).thenReturn(userWithRole(UserRoleDto.SUPER));

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }

    @Test
    void createUser_withRoleUserUsingAdmin_success() throws MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.USER);
        final User createdUser = newUser();
        when(userDao.create(any(User.class))).thenReturn(createdUser);
        when(jwtProvider.getCurrentUser()).thenReturn(userWithRole(UserRoleDto.ADMIN));

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }

    @Test
    void createUser_withRoleUserUsingUser_success() throws MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.USER);
        final User createdUser = newUser();
        when(userDao.create(any(User.class))).thenReturn(createdUser);
        when(jwtProvider.getCurrentUser()).thenReturn(userWithRole(UserRoleDto.USER));

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }

    @Test
    void createUser_withRoleUserUsingUnauthenticatedUser_success() throws MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.USER);
        final User createdUser = newUser();
        when(userDao.create(any(User.class))).thenReturn(createdUser);
        when(jwtProvider.getCurrentUser()).thenReturn(null);

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }


    private User newUser() {
        final User user = new User();
        user.setId(getRandomLong());
        return user;
    }

    private UserDto userWithRole(final UserRoleDto role) {
        final UserDto user = new UserDto();
        user.setRole(role);
        return user;
    }


    private UserCreationDto getValidRegisterRequest(final UserRoleDto role) {
        final UserCreationDto registerRequest = new UserCreationDto();
        registerRequest.setRole(role);
        registerRequest.setFirstName("New first name");
        registerRequest.setLastName("New last name");
        registerRequest.setPassword("Password");
        registerRequest.setEmail("email@test.com");

        return registerRequest;
    }
}
