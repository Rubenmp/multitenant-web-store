package com.mws.back_end.framework;

import com.mws.back_end.account.interfaces.user.dto.LoginRequest;
import com.mws.back_end.account.interfaces.user.dto.UserAuthenticationResponse;
import com.mws.back_end.account.interfaces.user.dto.UserRoleDto;
import com.mws.back_end.account.service.security.JwtCipher;
import com.mws.back_end.framework.dto.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.util.Pair;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.mws.back_end.account.interfaces.user.UserInterface.LOGIN_USER_URL;
import static com.mws.back_end.framework.dto.WebResultCode.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTestConfig extends TestUtils {
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

    @LocalServerPort
    protected int randomServerPort;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    private JwtCipher jwtCipher;


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


    protected LoginRequest newLoginRequest(final UserRoleDto role) {
        final LoginRequest loginRequest = new LoginRequest();
        String email = USER_EMAIL;
        if (role == UserRoleDto.ADMIN) {
            email = USER_ADMIN_EMAIL;
        } else if (role == UserRoleDto.SUPER) {
            email = USER_SUPER_EMAIL;
        }
        loginRequest.setEmail(email);
        loginRequest.setPassword(USER_PASSWORD);

        return loginRequest;
    }

    // Token auxiliary methods
    protected HttpEntity<String> createSuperHttpEntity() {
        return createSuperHttpEntity(null);
    }

    protected HttpEntity<String> createSuperHttpEntity(final String body) {
        return createHttpEntityInternal(UserRoleDto.SUPER, body);
    }

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

    protected HttpEntity<String> createHttpEntityInternal(final String body) {
        final HttpHeaders requestHeaders = createHttpHeadersWithAuthorization(null);

        if (body == null) {
            return new HttpEntity<>(requestHeaders);
        }

        return new HttpEntity<>(body, requestHeaders);
    }

    private HttpHeaders createHttpHeadersWithAuthorization(final String authorization) {
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (authorization != null) {
            requestHeaders.add(HttpHeaders.AUTHORIZATION, authorization);
        }
        return requestHeaders;
    }

    private String getTokenAs(final UserRoleDto role) {
        if (role == UserRoleDto.USER && userToken != null && !jwtCipher.isTokenDateExpired(userToken)) {
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

        final HttpEntity<String> httpEntity = new HttpEntity<>(toJson(loginRequest), requestHeaders);
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

}
