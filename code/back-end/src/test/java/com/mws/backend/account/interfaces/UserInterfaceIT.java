package com.mws.backend.account.interfaces;


import com.mws.backend.account.interfaces.user.dto.UserCreationDto;
import com.mws.backend.account.interfaces.user.dto.UserUpdateDto;
import com.mws.backend.framework.IntegrationTestConfig;
import com.mws.backend.framework.dto.WebResult;
import com.mws.backend.framework.dto.WebResultCode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static com.mws.backend.account.interfaces.user.UserInterface.CREATE_USER_URL;
import static com.mws.backend.account.interfaces.user.UserInterface.UPDATE_USER_URL;
import static com.mws.backend.framework.IntegrationTestConfig.TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserInterfaceIT extends IntegrationTestConfig {

    @Test
    void createUser_happyPath_success() {
        final UserCreationDto registerDto = createUserCreationDto("test.email@test.com");
        final URI uri = getUri(CREATE_USER_URL);

        final ResponseEntity<String> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(registerDto),
                String.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response status");
        final WebResult<Long> result = toWebResult(responseEntity, Long.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");
        assertNotNull(result.getData(), "User id");
    }

    @Test
    void createUser_repeatedEmail_badRequest() {
        final UserCreationDto registerRequest = createUserCreationDto(USER_EMAIL);
        final URI uri = getUri(CREATE_USER_URL);

        final ResponseEntity<String> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(registerRequest),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Response status");
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

        final ResponseEntity<String> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                new HttpEntity<>(registerRequest),
                String.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response status");
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
        final Long userId = convertStringToObject(responseEntityCreate.getBody(), Long.class);
        assertEquals(HttpStatus.OK, responseEntityCreate.getStatusCode(), "Response status");
        assertNotNull(userId, "User id");

        final UserUpdateDto registerRequest = createUserUpdateDto();
        registerRequest.setEmail(duplicatedEmail);

        final ResponseEntity<String> responseEntity = restTemplate.exchange(
                getUri(UPDATE_USER_URL),
                HttpMethod.PUT,
                new HttpEntity<>(registerRequest),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Response status");
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

}
