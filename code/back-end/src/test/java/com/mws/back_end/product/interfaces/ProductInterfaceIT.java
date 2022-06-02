package com.mws.back_end.product.interfaces;


import com.mws.back_end.framework.IntegrationTestConfig;
import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.dto.WebResultCode;
import com.mws.back_end.product.interfaces.dto.ProductCreationDto;
import com.mws.back_end.product.interfaces.dto.ProductDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mws.back_end.framework.IntegrationTestConfig.TEST_PROFILE;
import static com.mws.back_end.product.interfaces.ProductInterface.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductInterfaceIT extends IntegrationTestConfig {
    protected static final Long PRODUCT_ID = 1L;
    protected static final Long DELETED_PRODUCT_ID = 3L;

    @Test
    void createProduct_happyPath_success() {
        final ProductCreationDto registerDto = createUserCreationDto("test.email@test.com");
        final HttpEntity<String> httpEntity = createAdminHttpEntity(toJson(registerDto));
        final URI uri = getUri(CREATE_PRODUCT_URL);

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                httpEntity,
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
        creationDto.setDescription("New product description");

        return creationDto;
    }

    @Test
    void updateProduct_happyPath_success() {
        final ProductUpdateDto productUpdateDto = createProductUpdateDto();
        final HttpEntity<String> httpEntity = createAdminHttpEntity(toJson(productUpdateDto));

        final URI uri = getUri(UPDATE_PRODUCT_URL);

        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final WebResult<Serializable> result = toWebResult(response);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");
    }

    private ProductUpdateDto createProductUpdateDto() {
        final ProductUpdateDto updateRequest = new ProductUpdateDto();
        updateRequest.setId(PRODUCT_ID);
        updateRequest.setName("New product name");
        updateRequest.setImage("New product image");
        updateRequest.setDescription("New product description");

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
                createAdminHttpEntity(),
                String.class);

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode(), "Delete response status");
        final WebResult<Serializable> deleteResult = toWebResult(deleteResponse);
        assertEquals(WebResultCode.SUCCESS, deleteResult.getCode(), "Delete result code");

        // List products step
        final URI getUri = getUri(LIST_PRODUCTS_URL, Pair.of("active", "true"));
        final ResponseEntity<String> getResponse = restTemplate.exchange(
                getUri,
                HttpMethod.GET,
                createAdminHttpEntity(),
                String.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode(), "Get response status");
        WebResult<ArrayList<ProductDto>> getResult = toWebResultWithList(getResponse, ProductDto.class);
        assertEquals(WebResultCode.SUCCESS, getResult.getCode(), "Get result code");
        final List<Long> productIds = getResult.getData().stream().map(ProductDto::getId).collect(Collectors.toList());

        assertFalse(productIds.isEmpty(), "Returned products emptiness");
        assertTrue(productIds.contains(PRODUCT_ID), "Returned products contain id " + PRODUCT_ID);
        assertFalse(productIds.contains(Long.valueOf(deletedProductId)), "Returned products do not contain removed product");
        assertFalse(productIds.contains(DELETED_PRODUCT_ID), "Returned products do not contain previously removed product");
    }

    @Test
    void listProducts_deleted() {
        final URI uri = getUri(LIST_PRODUCTS_URL, Pair.of("active", "false"));
        final ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Get response status");
        WebResult<ArrayList<ProductDto>> result = toWebResultWithList(response, ProductDto.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Get result code");
        List<Long> productIds = result.getData().stream().map(ProductDto::getId).collect(Collectors.toList());

        assertFalse(productIds.isEmpty(), "Returned products emptiness");
        assertTrue(productIds.contains(DELETED_PRODUCT_ID), "Returned products contain previously removed product");
        assertFalse(productIds.contains(PRODUCT_ID), "Returned products do not contain active product  " + PRODUCT_ID);

        final ProductDto product = result.getData().stream().filter(p -> DELETED_PRODUCT_ID.equals(p.getId())).findFirst().orElse(null);
        assertNotNull(product, "Product");
        assertEquals("Deleted product", product.getName(), "Product name");
        assertEquals("image3", product.getImage(), "Product image");
        assertEquals("desc.3", product.getDescription(), "Product description");
    }

}
