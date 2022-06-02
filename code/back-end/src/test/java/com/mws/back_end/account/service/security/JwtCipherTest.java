package com.mws.back_end.account.service.security;

import com.mws.back_end.account.interfaces.user.dto.UserDto;
import com.mws.back_end.account.interfaces.user.dto.UserRoleDto;
import com.mws.back_end.framework.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtCipherTest extends TestUtils {
    @InjectMocks
    @Spy
    private JwtCipher jwtCipher;

    @Test
    void createToken_withRoleUser_success() {
        final UserDto user = newUserDto(UserRoleDto.USER);

        final String token = createToken(user);

        assertFalse(token.isBlank(), "Token is not blank");
    }


    @Test
    void isValidToken_withRoleUser_success() {
        final UserDto user = newUserDto(UserRoleDto.USER);
        final String token = createToken(user);
        Mockito.doReturn(true).when(jwtCipher).jwtRestrictionsEnabled();

        assertTrue(jwtCipher.isValidToken(token), "Valid token");
    }


    @Test
    void isTokenDateExpired_negative() {
        final UserDto user = newUserDto(UserRoleDto.USER);
        final String token = createToken(user);
        Mockito.doReturn(true).when(jwtCipher).jwtRestrictionsEnabled();

        assertFalse(jwtCipher.isTokenDateExpired(token), "Token expiration");
    }


    @Test
    void getCurrentUserRole_withRoleUser_success() {
        final UserDto user = newUserDto(UserRoleDto.USER);
        final String token = createToken(user);
        Mockito.doReturn(true).when(jwtCipher).jwtRestrictionsEnabled();
        Mockito.doReturn(token).when(jwtCipher).getCurrentTokenWithoutValidation();

        assertEquals(UserRoleDto.USER, jwtCipher.getCurrentUserRole(), "User role");
    }


    @Test
    void getCurrentUserRole_withRoleAdmin_success() {
        final UserDto user = newUserDto(UserRoleDto.ADMIN);
        final String token = createToken(user);
        Mockito.doReturn(true).when(jwtCipher).jwtRestrictionsEnabled();
        Mockito.doReturn(token).when(jwtCipher).getCurrentTokenWithoutValidation();

        assertEquals(UserRoleDto.ADMIN, jwtCipher.getCurrentUserRole(), "User role");
    }


    @Test
    void getCurrentUserRole_withRoleSuper_success() {
        final UserDto user = newUserDto(UserRoleDto.SUPER);
        final String token = createToken(user);
        Mockito.doReturn(true).when(jwtCipher).jwtRestrictionsEnabled();
        Mockito.doReturn(token).when(jwtCipher).getCurrentTokenWithoutValidation();

        assertEquals(UserRoleDto.SUPER, jwtCipher.getCurrentUserRole(), "User role");
    }


    @Test
    void getCurrentUserRole_withoutUser_success() {
        Mockito.doReturn(true).when(jwtCipher).jwtRestrictionsEnabled();
        Mockito.doReturn(null).when(jwtCipher).getCurrentTokenWithoutValidation();

        assertNull(jwtCipher.getCurrentUserRole(), "User role");
    }


    private String createToken(UserDto user) {
        final String token = jwtCipher.createToken(user);
        assertNotNull(token, "Token");
        return token;
    }

    private UserDto newUserDto(final UserRoleDto role) {
        final UserDto user = new UserDto();
        user.setTenantId(TENANT_ID);
        user.setId(getRandomLong());
        user.setRole(role);
        user.setEmail("user@mwstest.com");
        user.setFirstName("First name");
        user.setLastName("Last name");

        return user;
    }
}
