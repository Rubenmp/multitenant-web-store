package com.mws.back_end.account.interfaces;


import com.mws.back_end.account.interfaces.tenant.tenant.TenantDto;
import com.mws.back_end.account.interfaces.tenant.tenant.TenantUpdateDto;
import com.mws.back_end.account.service.security.JwtCipher;
import com.mws.back_end.framework.IntegrationTestConfig;
import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.dto.WebResultCode;
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
import java.util.ArrayList;

import static com.mws.back_end.account.interfaces.tenant.TenantInterface.*;
import static com.mws.back_end.framework.IntegrationTestConfig.TEST_PROFILE;
import static com.mws.back_end.framework.dto.WebResultCode.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TenantInterfaceIT extends IntegrationTestConfig {
    protected static final Long DELETED_TENANT_ID = 2L;

    @Autowired
    private JwtCipher jwtCipher;

    @Test
    void createTenant_happyPath_success() {
        final URI uri = getUri(CREATE_TENANT_URL, Pair.of("name", "New tenant"));
        final HttpEntity<String> httpRequest = this.createUserHttpEntity();

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                httpRequest,
                String.class);

        checkTenantWasCreated(response);
    }

    private Long checkTenantWasCreated(ResponseEntity<String> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final WebResult<Long> result = toWebResult(response, Long.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");
        assertNotNull(result.getData(), "Tenant id");

        return result.getData();
    }

    @Test
    void createTenant_repeatedName_badRequest() {
        final URI uri = getUri(CREATE_TENANT_URL, Pair.of("name", "MWS Tenant"));

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response status");

        final WebResult<Long> result = toWebResult(response, Long.class);
        assertEquals(WebResultCode.ERROR_INVALID_PARAMETER, result.getCode(), "Result code");
        assertEquals("Duplicate entry 'MWS Tenant'", result.getMessage(), "Result message");
        assertNull(result.getData(), "Tenant id");
    }

    @Test
    void updateTenant_happyPath_success() {
        final URI uri = getUri(CREATE_TENANT_URL, Pair.of("name", "Tenant to update"));

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                null,
                String.class);

        final Long createdTenantId = checkTenantWasCreated(response);

        final TenantUpdateDto updateRequest = createTenantUpdateDto(createdTenantId);
        final URI updateUri = getUri(UPDATE_TENANT_URL);
        final ResponseEntity<String> updateResponse = restTemplate.exchange(
                updateUri,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                String.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode(), "Response status");
        final WebResult<Serializable> result = toWebResult(updateResponse, Serializable.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");
    }


    private TenantUpdateDto createTenantUpdateDto(final Long tenantId) {
        final TenantUpdateDto updateRequest = new TenantUpdateDto();
        updateRequest.setId(tenantId);
        updateRequest.setName("Updated tenant name");

        return updateRequest;
    }


    @Test
    void listTenants_success() {
        final URI uri = getUri(LIST_TENANTS_URL, Pair.of("active", "true"));

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        WebResult<ArrayList<TenantDto>> authenticationResult = toWebResultWithList(response, TenantDto.class);
        final ArrayList<TenantDto> tenants = authenticationResult.getData();
        assertFalse(tenants.isEmpty(), "Tenant emptiness");
        assertTrue(tenants.stream().anyMatch(t -> TENANT_ID.equals(t.getId())), "Tenants contains tenant with id " + TENANT_ID);
        assertFalse(tenants.stream().anyMatch(t -> DELETED_TENANT_ID.equals(t.getId())), "Tenants does not contain deleted tenant " + DELETED_TENANT_ID);
        assertEquals(0, tenants.stream().filter(t -> !t.isActive()).count(), "Number of inactive tenants");
    }


    @Test
    void deleteTenant_validId_success() {
        final URI uri = getUri(DELETE_TENANT_URL, Pair.of("id", "3"));

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final WebResult<Serializable> result = toWebResult(response);
        assertEquals(SUCCESS, result.getCode(), "Result code");
        assertNull(result.getData(), "Data null");
    }
}
