package com.mws.backend.framework;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IntegrationTestConfig {
    private static final String USER_PASSWORD = "Password1";
    private static String userToken;
    private static ObjectMapper objectMapper;

    protected static final String USER_EMAIL = "email-user@test.com";
    protected static boolean forceUserTokenRefresh = false;
    public static final String TEST_PROFILE = "test";

    @LocalServerPort
    protected int randomServerPort;

    @Autowired
    private TestRestTemplate restTemplate;


    public String getTestUri() {
        return "http://localhost:" + randomServerPort;
    }

    public URI getUriFromEndpoint(final String endpoint) {
        return UriComponentsBuilder.fromUriString(getTestUri())
                .path(endpoint)
                .build()
                .encode()
                .toUri();
    }


    protected HttpEntity<String> createUserHttpEntityWithBody(final String body) {
        final HttpHeaders requestHeaders = createHttpHeaders();

        if (body == null) {
            return new HttpEntity<>(requestHeaders);
        }

        return new HttpEntity<>(body, requestHeaders);
    }

    protected HttpHeaders createHttpHeaders() {
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        return requestHeaders;
    }


    protected HttpEntity<String> createUserHttpEntity() {
        return createUserHttpEntityWithBody(null);
    }


    private static ObjectMapper getObjectMapper() {
        if (objectMapper != null) {
            return objectMapper;
        }

        objectMapper = new ObjectMapper();
        configureObjectMapperDateTimeFormatterToISO(objectMapper);
        return objectMapper;
    }


    private static void configureObjectMapperDateTimeFormatterToISO(final ObjectMapper objectMapper) {
        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_DATE));

        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }


    protected String convertObjectToString(final Object objectToConvert) {
        String convertedObject = "{}";
        try {
            convertedObject = getObjectMapper().writeValueAsString(objectToConvert);
        } catch (JsonProcessingException ignored) {
        }

        return convertedObject;
    }

    // Important: We need @NoArgsConstructor in valueType class
    protected <T> T convertStringToObject(final String data, final Class<T> valueType) {
        final ObjectMapper mapper = new ObjectMapper();
        T object = null;

        try {
            object = mapper.readValue(data, valueType);
        } catch (JsonProcessingException ignored) {
        }

        return object;
    }
}
