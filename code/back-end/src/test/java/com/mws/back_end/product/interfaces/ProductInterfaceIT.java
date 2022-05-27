package com.mws.back_end.product.interfaces;


import com.mws.back_end.framework.IntegrationTestConfig;
import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.dto.WebResultCode;
import com.mws.back_end.product.interfaces.dto.ProductCreationDto;
import com.mws.back_end.product.interfaces.dto.ProductUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.Serializable;
import java.net.URI;

import static com.mws.back_end.framework.IntegrationTestConfig.TEST_PROFILE;
import static com.mws.back_end.product.interfaces.ProductInterface.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductInterfaceIT extends IntegrationTestConfig {

    @Test
    void createProduct_happyPath_success() {
        final ProductCreationDto registerDto = createUserCreationDto("test.email@test.com");
        final URI uri = getUri(CREATE_PRODUCT_URL);

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(registerDto),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final WebResult<Long> result = toWebResult(response, Long.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");
        assertNotNull(result.getData(), "Product id");
    }

    private ProductCreationDto createUserCreationDto(final String name) {
        final ProductCreationDto creationDto = new ProductCreationDto();
        creationDto.setName(name);
        creationDto.setImage("New product image");

        return creationDto;
    }

    @Test
    void updateProduct_happyPath_success() {
        final ProductUpdateDto registerRequest = createProductUpdateDto();
        final URI uri = getUri(UPDATE_PRODUCT_URL);

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                new HttpEntity<>(registerRequest),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final WebResult<Serializable> result = toWebResult(response, Serializable.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");
    }

    private ProductUpdateDto createProductUpdateDto() {
        final ProductUpdateDto updateRequest = new ProductUpdateDto();
        updateRequest.setId(PRODUCT_ID);
        updateRequest.setName("New product name");
        updateRequest.setImage("New product image");

        return updateRequest;
    }

    @Test
    void deleteProduct_thenListAvailableProducts_withoutDeletedProduct() {
        // Delete product step
        final String deletedProductId = "2";
        final URI deleteUri = getUri(DELETE_PRODUCT_URL, Pair.of("id", deletedProductId));

        final ResponseEntity<String> deleteResponse = restTemplate.exchange(
                deleteUri,
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode(), "Delete response status");
        final WebResult<Serializable> deleteResult = toWebResult(deleteResponse, Serializable.class);
        assertEquals(WebResultCode.SUCCESS, deleteResult.getCode(), "Delete result code");

        // List products step
        final URI getUri = getUri(GET_PRODUCTS_URL, Pair.of("active", "true"));
        final ResponseEntity<String> getResponse = restTemplate.exchange(
                getUri,
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode(), "Get response status");
        final WebResult<Serializable> getResult = toWebResult(getResponse, Serializable.class);
        assertEquals(WebResultCode.SUCCESS, getResult.getCode(), "Get result code");
    }

}
