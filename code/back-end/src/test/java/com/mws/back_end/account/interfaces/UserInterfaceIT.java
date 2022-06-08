package com.mws.back_end.account.interfaces;


import com.mws.back_end.account.interfaces.user.dto.*;
import com.mws.back_end.account.service.security.JwtCipher;
import com.mws.back_end.framework.IntegrationTestConfig;
import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.dto.WebResultCode;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.Serializable;
import java.net.URI;
import java.util.Optional;

import static com.mws.back_end.account.interfaces.user.UserInterface.*;
import static com.mws.back_end.framework.IntegrationTestConfig.TEST_PROFILE;
import static com.mws.back_end.framework.dto.WebResultCode.ERROR_AUTH;
import static com.mws.back_end.framework.dto.WebResultCode.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserInterfaceIT extends IntegrationTestConfig {

    @Autowired
    private JwtCipher jwtCipher;

    @Test
    void createUser_thenLogin_success() {
        final UserCreationDto creationDto = createUserCreationDto("createUser_thenLogin_success@mwstest.com");
        final ResponseEntity<String> creationResponse = restTemplate.exchange(
                getUri(CREATE_USER_URL),
                HttpMethod.POST,
                new HttpEntity<>(creationDto),
                String.class);

        final Long userId = checkUserWasCreated(creationResponse);

        final LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(creationDto.getEmail());
        loginRequest.setPassword(creationDto.getPassword());
        final ResponseEntity<String> loginResponse = restTemplate.exchange(
                getUri(LOGIN_USER_URL),
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                String.class);

        final String token = checkLogin(loginResponse);
        checkToken(token, creationDto.getEmail(), userId);
    }

    private String checkLogin(ResponseEntity<String> loginResponse) {
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode(), "Response status");
        final WebResult<UserAuthenticationResponse> authenticationResult = toWebResult(loginResponse, UserAuthenticationResponse.class);
        assertEquals(SUCCESS, authenticationResult.getCode(), "Result code");
        assertNotNull(authenticationResult.getData().getFirstName(), "First name cannot be null");
        assertNotNull(authenticationResult.getData().getLastName(), "Last name cannot be null");

        return authenticationResult.getData().getToken();
    }

    private Long checkUserWasCreated(ResponseEntity<String> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final WebResult<Long> result = toWebResult(response, Long.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");
        assertNotNull(result.getData(), "User id");

        return result.getData();
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
        assertEquals("Duplicate entry 'user@mwstest.com'", result.getMessage(), "Result message");
        assertNull(result.getData(), "User id");
    }

    private UserCreationDto createUserCreationDto(final String email) {
        final UserCreationDto creationDto = new UserCreationDto();
        creationDto.setTenantId(TENANT_ID);
        creationDto.setRole(UserRoleDto.USER);
        creationDto.setFirstName("New first name");
        creationDto.setLastName("New last name");
        creationDto.setEmail(email);
        creationDto.setPassword("Password1");

        return creationDto;
    }

    @Test
    void updateUser_thenLogin_success() {
        final UserCreationDto registerDto = createUserCreationDto("updateUser_happyPath_success@test.com");
        final ResponseEntity<String> response = restTemplate.exchange(
                getUri(CREATE_USER_URL),
                HttpMethod.POST,
                new HttpEntity<>(registerDto),
                String.class);

        final Long createdUserId = checkUserWasCreated(response);

        final UserUpdateDto updateRequest = createUserUpdateDto(createdUserId);
        final ResponseEntity<String> updateResponse = restTemplate.exchange(
                getUri(UPDATE_USER_URL),
                HttpMethod.PUT,
                createUserHttpEntity(toJson(updateRequest)),
                String.class);

        checkSuccess(updateResponse);

        final LoginRequest loginRequest = toLoginRequest(updateRequest);
        final ResponseEntity<String> loginResponse = restTemplate.exchange(
                getUri(LOGIN_USER_URL),
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                String.class);

        final String token = checkLogin(loginResponse);
        checkToken(token, loginRequest.getEmail(), createdUserId);
    }

    private void checkSuccess(ResponseEntity<String> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final WebResult<Serializable> result = toWebResult(response, Serializable.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");
    }

    private LoginRequest toLoginRequest(UserUpdateDto updateRequest) {
        final LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(updateRequest.getEmail());
        loginRequest.setPassword(updateRequest.getPassword());

        return loginRequest;
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

        final UserUpdateDto updateDto = createUserUpdateDto(USER_ID);
        updateDto.setEmail(duplicatedEmail);

        final ResponseEntity<String> responseEntityUpdate = restTemplate.exchange(
                getUri(UPDATE_USER_URL),
                HttpMethod.PUT,
                new HttpEntity<>(updateDto),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntityUpdate.getStatusCode(), "Update status");
        final WebResult<Serializable> resultUpdate = toWebResult(responseEntityUpdate, Serializable.class);
        assertEquals(WebResultCode.ERROR_INVALID_PARAMETER, resultUpdate.getCode(), "Update user result code");
    }

    private UserUpdateDto createUserUpdateDto(final Long userId) {
        final UserUpdateDto updateRequest = new UserUpdateDto();
        updateRequest.setId(userId);
        updateRequest.setFirstName("New first name");
        updateRequest.setLastName("New last name");
        updateRequest.setEmail("new.email@test.com");
        updateRequest.setPassword("Password2");

        return updateRequest;
    }


    @Test
    void login_validPassword_success() {
        final LoginRequest loginRequest = newLoginRequest(UserRoleDto.USER);
        final HttpEntity<LoginRequest> httpEntity = new HttpEntity<>(loginRequest);
        final URI uri = getUri(LOGIN_USER_URL);

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                httpEntity,
                String.class);

        final String token = checkLogin(response);
        checkToken(token, USER_EMAIL, USER_ID);
    }


    @Test
    void login_invalidPassword_error() {
        final LoginRequest loginRequest = newLoginRequest(UserRoleDto.USER);
        loginRequest.setPassword(loginRequest.getPassword() + "error");
        final HttpEntity<LoginRequest> httpEntity = new HttpEntity<>(loginRequest);
        final URI uri = getUri(LOGIN_USER_URL);

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response status");
        WebResult<UserAuthenticationResponse> authenticationResult = toWebResult(response, UserAuthenticationResponse.class);
        assertEquals(ERROR_AUTH, authenticationResult.getCode());
    }

    private void checkToken(final String token, final String expectedEmail, final Long userId) {
        assertTrue(Strings.isNotBlank(token), "Token not empty");
        assertTrue(jwtCipher.isValidToken(token), "Valid token");
        final Optional<String> loginEmailOpt = jwtCipher.getLoginEmail(token);
        assertTrue(loginEmailOpt.isPresent(), "Login email is present in token");
        assertEquals(expectedEmail, loginEmailOpt.get(), "User login email");

        assertEquals(userId, jwtCipher.getUserId(token), "Token user id");
        assertEquals(TENANT_ID, jwtCipher.getTenantId(token), "Token tenant id");
        assertEquals(UserRoleDto.USER, jwtCipher.getUserRole(token), "Token user role");
    }


    @Test
    void getUser_validId_success() {
        final URI uri = getUri(GET_USER_URL, Pair.of("id", USER_ID.toString()));

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final WebResult<UserDto> result = toWebResult(response, UserDto.class);
        assertEquals(SUCCESS, result.getCode(), "Result code");
        assertNotNull(result.getData(), "Data not null");
        assertEquals(USER_ID, result.getData().getId(), "User id");
    }
}
