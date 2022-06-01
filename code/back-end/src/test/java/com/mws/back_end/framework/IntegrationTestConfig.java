package com.mws.back_end.framework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.mws.back_end.account.interfaces.user.dto.LoginRequest;
import com.mws.back_end.account.interfaces.user.dto.UserAuthenticationResponse;
import com.mws.back_end.account.interfaces.user.dto.UserRoleDto;
import com.mws.back_end.account.service.security.JwtProvider;
import com.mws.back_end.framework.dto.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.util.Pair;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.mws.back_end.account.interfaces.user.UserInterface.LOGIN_USER_URL;
import static com.mws.back_end.framework.dto.WebResultCode.SUCCESS;
import static com.mws.back_end.framework.utils.DateUtils.isDateBeforeNow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class IntegrationTestConfig {
    protected static final Long TENANT_ID = 1L;
    protected static final Long USER_ID = 1L;
    protected static final Long PRODUCT_ID = 1L;
    protected static final String USER_EMAIL = "user@mwstest.com";
    protected static final String USER_ADMIN_EMAIL = "admin@mwstest.com";
    protected static final String USER_SUPER_EMAIL = "super@mwstest.com";
    private static final String USER_PASSWORD = "Password1";

    public static final String TEST_PROFILE = "test";

    private static String userToken;
    private static String adminToken;
    private static String superToken;
    private static ObjectMapper objectMapper;

    @LocalServerPort
    protected int randomServerPort;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    private JwtProvider jwtProvider;


    public String getTestUri() {
        return "http://localhost:" + randomServerPort;
    }

    public URI getUri(final String endpoint) {
        return getUri(endpoint, null);
    }

    @SafeVarargs
    public final URI getUri(final String endpoint, Pair<String, String>... params) {
        UriComponentsBuilder path = UriComponentsBuilder.fromUriString(getTestUri())
                .path(endpoint);
        if (params != null) {
            for (Pair<String, String> param : params) {
                path.queryParam(param.getFirst(), param.getSecond());
            }
        }
        return path.build()
                .encode()
                .toUri();
    }

    protected WebResult<Serializable> toWebResult(final ResponseEntity<String> responseEntity) {
        return toWebResult(responseEntity, Serializable.class);
    }

    protected <E extends Serializable> WebResult<E> toWebResult(final ResponseEntity<String> response, final Class<E> dataClass) {
        WebResult<E> result = convertStringToObject(response.getBody(), WebResult.class);
        if (result.getData() instanceof Map) {
            final String dataJson = toJson((Map<String, Object>) result.getData());
            E data = convertStringToObject(dataJson, dataClass);
            result.setData(data);
        } else if (result.getData() != null) {
            E data = convertStringToObject(result.getData().toString(), dataClass);
            result.setData(data);
        }

        return result;
    }

    protected <E extends Serializable> WebResult<ArrayList<E>> toWebResultWithList(final ResponseEntity<String> response, final Class<E> dataClass) {
        WebResult<ArrayList<E>> result = convertStringToObject(response.getBody(), WebResult.class);
        if (result.getData() != null) {
            ArrayList<E> actualData = (ArrayList<E>) ((List<?>) result.getData()).stream().map(t -> (Map<String, Object>) t).map(this::toJson)
                    .map(json -> convertStringToObject(json, dataClass)).collect(Collectors.toList());

            result.setData(actualData);
        }

        return result;
    }

    private String toJson(Map<String, Object> properties) {
        int i = 0;
        final int mapSize = properties.size();

        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":");

            if (entry.getValue() != null) {
                json.append("\"").append(entry.getValue().toString()).append("\"");
            } else {
                json.append("null");
            }

            if (i < (mapSize - 1)) {
                json.append(",");
            }
            i++;
        }
        json.append("}");

        return json.toString();
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


    protected String convertToJson(final Object objectToConvert) {
        String convertedObject = "{}";
        try {
            convertedObject = getObjectMapper().writeValueAsString(objectToConvert);
        } catch (JsonProcessingException ignored) {}

        return convertedObject;
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


    protected LoginRequest newLoginRequest(final UserRoleDto role) {
        final LoginRequest loginRequest = new LoginRequest();
        String email = USER_EMAIL;
        if (role == UserRoleDto.ADMIN) {
            email = USER_ADMIN_EMAIL;
        } else if  (role == UserRoleDto.SUPER) {
            email = USER_SUPER_EMAIL;
        }
        loginRequest.setEmail(email);
        loginRequest.setPassword(USER_PASSWORD);

        return loginRequest;
    }

    // Token auxiliary methods
    protected HttpEntity<String> createAdminHttpEntity() {
        return createAdminHttpEntity(null);
    }

    protected HttpEntity<String> createAdminHttpEntity(final String body) {
        return createHttpEntityInternal(UserRoleDto.ADMIN, body);
    }

    protected HttpEntity<String> createUserHttpEntity() {
        return createUserHttpEntity(null);
    }

    protected HttpEntity<String> createUserHttpEntity(final String body) {
        return createHttpEntityInternal(UserRoleDto.USER, body);
    }

    private HttpEntity<String> createHttpEntityInternal(final UserRoleDto role, final String body) {
        final HttpHeaders requestHeaders = createHttpHeadersWithAuthorization(getTokenAs(role));

        if (body == null) {
            return new HttpEntity<>(requestHeaders);
        }

        return new HttpEntity<>(body, requestHeaders);
    }

    private HttpHeaders createHttpHeadersWithAuthorization(final String authorization) {
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.add(HttpHeaders.AUTHORIZATION, authorization);

        return requestHeaders;
    }

    private String getTokenAs(final UserRoleDto role) {
        if (role == UserRoleDto.USER && userToken != null && !isTokenExpired(userToken)) {
            return userToken;
        }

        final String token = loginAs(role).getToken();
        if (role == UserRoleDto.USER) {
            userToken = token;
            return userToken;
        } else if (role == UserRoleDto.ADMIN) {
            adminToken = token;
            return adminToken;
        } else if (role == UserRoleDto.SUPER) {
            superToken = token;
            return superToken;
        }
        return null;
    }

    private UserAuthenticationResponse loginAs(final UserRoleDto role) {
        final LoginRequest loginRequest = newLoginRequest(role);
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        final HttpEntity<String> httpEntity = new HttpEntity<>(convertToJson(loginRequest), requestHeaders);
        final URI uri = getUri(LOGIN_USER_URL);

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final WebResult<UserAuthenticationResponse> authenticationResult = toWebResult(response, UserAuthenticationResponse.class);
        assertEquals(SUCCESS, authenticationResult.getCode(), "Authentication result code");
        return authenticationResult.getData();
    }

    private boolean isTokenExpired(final String userToken) {
        final Optional<Date> expirationDate = jwtProvider.getExpirationDateFromJwt(userToken);
        if (expirationDate.isEmpty()) {
            return true;
        }
        return isDateBeforeNow(expirationDate.get());
    }

}
