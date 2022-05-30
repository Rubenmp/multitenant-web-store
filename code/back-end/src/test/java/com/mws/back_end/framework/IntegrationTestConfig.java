package com.mws.back_end.framework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mws.back_end.account.interfaces.user.dto.LoginRequest;
import com.mws.back_end.framework.dto.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

public class IntegrationTestConfig {
    protected static final Long TENANT_ID = 1L;
    protected static final Long DELETED_TENANT_ID = 2L;
    protected static final Long USER_ID = 1L;
    protected static final String USER_EMAIL = "user@mwstest.com";
    private static final String USER_PASSWORD = "Password1";

    public static final String TEST_PROFILE = "test";

    @LocalServerPort
    protected int randomServerPort;

    @Autowired
    protected TestRestTemplate restTemplate;


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


    protected LoginRequest newLoginRequestForUser() {
        final LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(USER_EMAIL);
        loginRequest.setPassword(USER_PASSWORD);

        return loginRequest;
    }
}
