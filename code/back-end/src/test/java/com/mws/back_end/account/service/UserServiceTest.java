package com.mws.back_end.account.service;

import com.mws.back_end.account.interfaces.tenant.tenant.TenantDto;
import com.mws.back_end.account.interfaces.user.dto.UserCreationDto;
import com.mws.back_end.account.interfaces.user.dto.UserRoleDto;
import com.mws.back_end.account.interfaces.user.dto.UserUpdateDto;
import com.mws.back_end.account.model.dao.UserDao;
import com.mws.back_end.account.model.entity.User;
import com.mws.back_end.account.model.entity.UserRole;
import com.mws.back_end.account.service.security.JwtService;
import com.mws.back_end.framework.TestUtils;
import com.mws.back_end.framework.exception.MWSException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest extends TestUtils {

    @Mock
    private UserDao userDao;

    @Mock
    private JwtService jwtService;

    @Mock
    private TenantService tenantService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_withRoleUser_success() throws MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.USER);
        final User createdUser = new User();
        createdUser.setId(getRandomLong());
        when(userDao.create(any(User.class))).thenReturn(createdUser);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }

    @Test
    void createUser_withoutEmail_error() {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.USER);
        registerRequest.setEmail(null);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

        createUserExpectingException(registerRequest);
    }

    @Test
    void createUser_withEmptyEmail_error() {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.USER);
        registerRequest.setEmail("");
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

        createUserExpectingException(registerRequest);
    }

    @Test
    void createUser_withoutPassword_error() {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.USER);
        registerRequest.setPassword(null);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

        createUserExpectingException(registerRequest);
    }

    @Test
    void createUser_withEmptyPassword_error() {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.USER);
        registerRequest.setPassword("");
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

        createUserExpectingException(registerRequest);
    }

    private void createUserExpectingException(UserCreationDto registerRequest) {
        boolean exceptionThrown = false;
        try {
            userService.createUser(registerRequest);
        } catch (MWSException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown, "Exception should be thrown");
    }

    @Test
    void createUser_withRoleSuperUsingSuper_success() throws MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.SUPER);
        final User createdUser = newUser();
        when(userDao.create(any(User.class))).thenReturn(createdUser);
        when(jwtService.getCurrentUserRole()).thenReturn(UserRoleDto.SUPER);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }

    @Test
    void createUser_withRoleSuperUsingAdmin_notAllowed() {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.SUPER);
        when(jwtService.getCurrentUserRole()).thenReturn(UserRoleDto.ADMIN);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

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
        when(jwtService.getCurrentUserRole()).thenReturn(null);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

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
        when(jwtService.getCurrentUserRole()).thenReturn(null);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

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
        when(jwtService.getCurrentUserRole()).thenReturn(UserRoleDto.SUPER);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }


    @Test
    void createUser_withRoleAdminUsingAdmin_success() throws MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.ADMIN);
        final User createdUser = newUser();
        when(userDao.create(any(User.class))).thenReturn(createdUser);
        when(jwtService.getCurrentUserRole()).thenReturn(UserRoleDto.ADMIN);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }


    @Test
    void createUser_withRoleAdminUsingUser_notAllowed() {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.ADMIN);
        when(jwtService.getCurrentUserRole()).thenReturn(UserRoleDto.USER);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

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
        when(jwtService.getCurrentUserRole()).thenReturn(null);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

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
        when(jwtService.getCurrentUserRole()).thenReturn(UserRoleDto.SUPER);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }

    @Test
    void createUser_withRoleUserUsingAdmin_success() throws MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.USER);
        final User createdUser = newUser();
        when(userDao.create(any(User.class))).thenReturn(createdUser);
        when(jwtService.getCurrentUserRole()).thenReturn(UserRoleDto.ADMIN);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }

    @Test
    void createUser_withRoleUserUsingUser_success() throws MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.USER);
        final User createdUser = newUser();
        when(userDao.create(any(User.class))).thenReturn(createdUser);
        when(jwtService.getCurrentUserRole()).thenReturn(UserRoleDto.USER);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }

    @Test
    void createUser_withRoleUserUsingUnauthenticatedUser_success() throws MWSException {
        final UserCreationDto registerRequest = getValidRegisterRequest(UserRoleDto.USER);
        final User createdUser = newUser();
        when(userDao.create(any(User.class))).thenReturn(createdUser);
        when(jwtService.getCurrentUserRole()).thenReturn(null);
        when(tenantService.getTenant(TENANT_ID)).thenReturn(new TenantDto());

        final Long createdUserId = userService.createUser(registerRequest);

        assertEquals(createdUser.getId(), createdUserId, "User id");
    }


    private User newUser() {
        final User user = new User();
        user.setId(getRandomLong());
        return user;
    }

    private User userWithRole(final UserRole role) {
        final User user = new User();
        user.setId(getRandomLong());
        user.setRole(role);
        return user;
    }

    private UserCreationDto getValidRegisterRequest(final UserRoleDto role) {
        final UserCreationDto registerRequest = new UserCreationDto();
        registerRequest.setTenantId(1L);
        registerRequest.setRole(role);
        registerRequest.setFirstName("New first name");
        registerRequest.setLastName("New last name");
        registerRequest.setPassword("Password");
        registerRequest.setEmail("email@test.com");

        return registerRequest;
    }


    @Test
    void updateUser_happyPathForUser_success() {
        final UserUpdateDto updateDto = getUpdateRequest();
        when(userDao.findWeak(updateDto.getId())).thenReturn(userWithRole(UserRole.USER));

        try {
            userService.updateUser(updateDto);
        } catch (MWSException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void updateUser_withoutEmail_error() {
        final UserUpdateDto updateDto = getUpdateRequest();
        updateDto.setEmail(null);

        updateUserExpectingException(updateDto);
    }

    @Test
    void updateUser_withEmptyEmail_error() {
        final UserUpdateDto updateDto = getUpdateRequest();
        updateDto.setEmail("");

        updateUserExpectingException(updateDto);
    }

    @Test
    void updateUser_withoutPassword_error() {
        final UserUpdateDto updateDto = getUpdateRequest();
        updateDto.setPassword(null);

        updateUserExpectingException(updateDto);
    }
    @Test
    void updateUser_withEmptyPassword_error() {
        final UserUpdateDto updateDto = getUpdateRequest();
        updateDto.setPassword("");

        updateUserExpectingException(updateDto);
    }

    private void updateUserExpectingException(UserUpdateDto updateDto) {
        boolean exceptionThrown = false;
        try {
            userService.updateUser(updateDto);
        } catch (MWSException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown, "Exception should be thrown");
    }

    @Test
    void updateUser_happyPathForAdmin_success() {
        final UserUpdateDto updateDto = getUpdateRequest();
        when(userDao.findWeak(updateDto.getId())).thenReturn(userWithRole(UserRole.ADMIN));

        try {
            userService.updateUser(updateDto);
        } catch (MWSException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void updateUser_happyPathForSuper_success() {
        final UserUpdateDto updateDto = getUpdateRequest();
        when(userDao.findWeak(updateDto.getId())).thenReturn(userWithRole(UserRole.SUPER));

        try {
            userService.updateUser(updateDto);
        } catch (MWSException e) {
            fail(e.getMessage());
        }
    }


    private UserUpdateDto getUpdateRequest() {
        final UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setId(getRandomLong());
        updateDto.setFirstName("New first name");
        updateDto.setLastName("New last name");
        updateDto.setPassword("Password");
        updateDto.setEmail("email@test.com");

        return updateDto;
    }
}
