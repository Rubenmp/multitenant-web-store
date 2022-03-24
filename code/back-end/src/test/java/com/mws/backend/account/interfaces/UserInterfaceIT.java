package com.mws.backend.account.interfaces;


import com.mws.backend.account.interfaces.user.dto.UserCreationDto;
import com.mws.backend.account.model.entity.User;
import com.mws.backend.framework.IntegrationTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static com.mws.backend.account.interfaces.user.UserInterface.CREATE_USER_URL;
import static com.mws.backend.framework.IntegrationTestConfig.TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserInterfaceIT extends IntegrationTestConfig {

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    @DisplayName("Create user")
    void createUser() {
        final UserCreationDto registerRequest = createRegisterRequest();
        final HttpEntity<UserCreationDto> httpEntity = new HttpEntity<>(registerRequest);
        final URI uri = getUriFromEndpoint(CREATE_USER_URL);

        final ResponseEntity<String> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                httpEntity,
                String.class);

        final User createdUser = convertStringToObject(responseEntity.getBody(), User.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response status");
        checkCreatedUser(registerRequest, createdUser);
    }

        private void checkCreatedUser(final UserCreationDto registerRequest, final User createdUser) {
        assertNotNull(createdUser, "Created user");
        assertNotNull(createdUser.getId(), "Created user id");
        assertEquals(registerRequest.getFirstName(), createdUser.getFirstName(), "Created user first name");
        assertEquals(registerRequest.getLastName(), createdUser.getLastName(), "Created user last name");
        assertEquals(registerRequest.getEmail(), createdUser.getEmail(), "Created user email");
    }

    private UserCreationDto createRegisterRequest() {
        final UserCreationDto registerRequest = new UserCreationDto();
        registerRequest.setFirstName("New first name");
        registerRequest.setLastName("New last name");
        registerRequest.setEmail("new.email@test.com");
        registerRequest.setPassword("Password1");

        return registerRequest;
    }

}
