package com.mws.back_end.account.interfaces;


import com.mws.back_end.account.interfaces.user.dto.LoginRequest;
import com.mws.back_end.account.interfaces.user.dto.UserAuthenticationResponse;
import com.mws.back_end.account.interfaces.user.dto.UserCreationDto;
import com.mws.back_end.account.interfaces.user.dto.UserUpdateDto;
import com.mws.back_end.account.service.JwtProvider;
import com.mws.back_end.framework.IntegrationTestConfig;
import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.dto.WebResultCode;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.Optional;

import static com.mws.back_end.account.interfaces.user.UserInterface.*;
import static com.mws.back_end.framework.IntegrationTestConfig.TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserInterfaceIT extends IntegrationTestConfig {

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void createUser_happyPath_success() {
        final UserCreationDto registerDto = createUserCreationDto("test.email@test.com");
        final URI uri = getUri(CREATE_USER_URL);

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(registerDto),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final WebResult<Long> result = toWebResult(response, Long.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");
        assertNotNull(result.getData(), "User id");
    }

    @Test
    void createUser_repeatedEmail_badRequest() {
        final UserCreationDto registerRequest = createUserCreationDto(USER_EMAIL);
        final URI uri = getUri(CREATE_USER_URL);

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(registerRequest),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response status");

        final WebResult<Long> result = toWebResult(response, Long.class);
        assertEquals(WebResultCode.ERROR_INVALID_PARAMETER, result.getCode(), "Result code");
        assertEquals("Duplicate entry 'user@mwstest.com'", result.getMessage(), "Result code");
        assertNull(result.getData(), "User id");
    }

    private UserCreationDto createUserCreationDto(final String email) {
        final UserCreationDto creationDto = new UserCreationDto();
        creationDto.setFirstName("New first name");
        creationDto.setLastName("New last name");
        creationDto.setEmail(email);
        creationDto.setPassword("Password1");

        return creationDto;
    }

    @Test
    void updateUser_happyPath_success() {
        final UserUpdateDto registerRequest = createUserUpdateDto();
        final URI uri = getUri(UPDATE_USER_URL);

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                new HttpEntity<>(registerRequest),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final WebResult<Serializable> result = toWebResult(response, Serializable.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");
    }

    @Test
    void updateUser_withRepeatedEmail_badRequest() {
        final String duplicatedEmail = "test.duplicateemail@test.com";
        final UserCreationDto registerDto = createUserCreationDto(duplicatedEmail);
        final ResponseEntity<String> responseEntityCreate = restTemplate.exchange(
                getUri(CREATE_USER_URL),
                HttpMethod.POST,
                new HttpEntity<>(registerDto),
                String.class);

        assertEquals(HttpStatus.OK, responseEntityCreate.getStatusCode(), "Creation status");
        final WebResult<Long> resultCreate = toWebResult(responseEntityCreate, Long.class);
        assertEquals(WebResultCode.SUCCESS, resultCreate.getCode(), "Create user result code");
        assertNotNull(resultCreate.getData(), "Created user id");

        final UserUpdateDto registerRequest = createUserUpdateDto();
        registerRequest.setEmail(duplicatedEmail);

        final ResponseEntity<String> responseEntityUpdate = restTemplate.exchange(
                getUri(UPDATE_USER_URL),
                HttpMethod.PUT,
                new HttpEntity<>(registerRequest),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntityUpdate.getStatusCode(), "Update status");
        final WebResult<Serializable> resultUpdate = toWebResult(responseEntityUpdate, Serializable.class);
        assertEquals(WebResultCode.ERROR_INVALID_PARAMETER, resultUpdate.getCode(), "Update user result code");
    }

    private UserUpdateDto createUserUpdateDto() {
        final UserUpdateDto updateRequest = new UserUpdateDto();
        updateRequest.setId(USER_ID);
        updateRequest.setFirstName("New first name");
        updateRequest.setLastName("New last name");
        updateRequest.setEmail("new.email@test.com");
        updateRequest.setPassword("Password2");

        return updateRequest;
    }


    @Test
    void login_validPassword_success() {
        final LoginRequest loginRequest = newLoginRequestForUser();
        final HttpEntity<LoginRequest> httpEntity = new HttpEntity<>(loginRequest);
        final URI uri = getUri(LOGIN_USER_URL);

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final UserAuthenticationResponse authenticationResponse = convertStringToObject(response.getBody(), UserAuthenticationResponse.class);
        assertNotNull(authenticationResponse, "Authentication response");

        final String token = authenticationResponse.getToken();
        checkUserToken(token);
    }


    @Test
    void login_invalidPassword_error() {
        final LoginRequest loginRequest = newLoginRequestForUser();
        loginRequest.setPassword(loginRequest.getPassword() + "error");
        final HttpEntity<LoginRequest> httpEntity = new HttpEntity<>(loginRequest);
        final URI uri = getUri(LOGIN_USER_URL);

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response status");
        final UserAuthenticationResponse authenticationResponse = convertStringToObject(response.getBody(), UserAuthenticationResponse.class);
        assertNotNull(authenticationResponse, "Authentication response");

        final String token = authenticationResponse.getToken();
        checkUserToken(token);
    }

    private void checkUserToken(final String token) {
        assertTrue(Strings.isNotBlank(token), "Token not empty");
        assertTrue(jwtProvider.isTokenWellFormedAndSigned(token), "Token well formed");
        assertTrue(jwtProvider.getExpirationDateFromJwt(token).filter(d -> d.after(new Date())).isPresent(),
                "Token expiration date after now");
        final Optional<String> loginEmailOpt = jwtProvider.getLoginEmailFromJwt(token);
        assertTrue(loginEmailOpt.isPresent(), "Login email is present in token");
        assertEquals(USER_EMAIL, loginEmailOpt.get(), "User login email");
    }
}
