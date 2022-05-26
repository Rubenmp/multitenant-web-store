package com.mws.back_end.product.interfaces;


import com.mws.back_end.framework.IntegrationTestConfig;
import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.dto.WebResultCode;
import com.mws.back_end.product.interfaces.dto.ProductCreationDto;
import com.mws.back_end.product.interfaces.dto.ProductUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.Serializable;
import java.net.URI;

import static com.mws.back_end.framework.IntegrationTestConfig.TEST_PROFILE;
import static com.mws.back_end.product.interfaces.ProductInterface.CREATE_PRODUCT_URL;
import static com.mws.back_end.product.interfaces.ProductInterface.UPDATE_PRODUCT_URL;
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
}
