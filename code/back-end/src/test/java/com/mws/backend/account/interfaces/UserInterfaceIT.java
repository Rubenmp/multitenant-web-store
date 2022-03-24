package com.mws.backend.account.interfaces;


import com.mws.backend.account.interfaces.user.dto.UserCreationDto;
import com.mws.backend.account.interfaces.user.dto.UserUpdateDto;
import com.mws.backend.framework.IntegrationTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static com.mws.backend.account.interfaces.user.UserInterface.CREATE_USER_URL;
import static com.mws.backend.framework.IntegrationTestConfig.TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserInterfaceIT extends IntegrationTestConfig {

    @Test
    void createUser() {
        final UserCreationDto registerRequest = createRegisterRequest("test.email@test.com");
        final URI uri = getUriFromEndpoint(CREATE_USER_URL);

        final ResponseEntity<String> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(registerRequest),
                String.class);

        final Long userId = convertStringToObject(responseEntity.getBody(), Long.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response status");
        assertNotNull(userId, "User id");
    }

    @Test
    void createUser_repeatedEmail_notPossible() {
        final UserCreationDto registerRequest = createRegisterRequest(USER_EMAIL);
        final URI uri = getUriFromEndpoint(CREATE_USER_URL);

        final ResponseEntity<String> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(registerRequest),
                String.class);

        assertNotEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response status");
    }

    private UserCreationDto createRegisterRequest(final String email) {
        final UserCreationDto registerRequest = new UserCreationDto();
        registerRequest.setFirstName("New first name");
        registerRequest.setLastName("New last name");
        registerRequest.setEmail(email);
        registerRequest.setPassword("Password1");

        return registerRequest;
    }

    @Test
    void updateUser() {
        final UserCreationDto registerRequest = createUserUpdateDto();
        final URI uri = getUriFromEndpoint(CREATE_USER_URL);

        final ResponseEntity<String> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(registerRequest),
                String.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response status");
    }

    private UserUpdateDto createUserUpdateDto() {
        final UserUpdateDto updateRequest = new UserUpdateDto();
        updateRequest.setId(USER_ID);
        updateRequest.setFirstName("New first name");
        updateRequest.setLastName("New last name");
        updateRequest.setEmail("new.email@test.com");
        updateRequest.setPassword("Password1");

        return updateRequest;
    }

}
