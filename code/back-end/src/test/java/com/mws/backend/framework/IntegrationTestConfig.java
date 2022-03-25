package com.mws.backend.framework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mws.backend.framework.dto.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.fail;

public class IntegrationTestConfig {
    protected static final Long USER_ID = 1L;
    protected static final String USER_EMAIL = "user@mwstest.com";
    public static final String TEST_PROFILE = "test";

    @LocalServerPort
    protected int randomServerPort;

    @Autowired
    protected TestRestTemplate restTemplate;


    public String getTestUri() {
        return "http://localhost:" + randomServerPort;
    }

    public URI getUri(final String endpoint) {
        return UriComponentsBuilder.fromUriString(getTestUri())
                .path(endpoint)
                .build()
                .encode()
                .toUri();
    }

    protected WebResult<?> toWebResult(final ResponseEntity<String> responseEntity) {
        return convertStringToObject(responseEntity.getBody(), WebResult.class);
    }

    // Important: We need @NoArgsConstructor in valueType class
    protected <T> T convertStringToObject(final String data, final Class<T> valueType) {
        final ObjectMapper mapper = new ObjectMapper();
        T object = null;

        try {
            object = mapper.readValue(data, valueType);
        } catch (JsonProcessingException e) {
            fail("It was not possible to parse object" + e);
        }

        return object;
    }
}
