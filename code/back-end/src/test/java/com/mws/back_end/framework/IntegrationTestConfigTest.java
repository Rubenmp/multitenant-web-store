package com.mws.back_end.framework;

import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.dto.WebResultCode;
import com.mws.back_end.product.interfaces.dto.ProductDto;
import com.mws.back_end.sales.interfaces.dto.OrderDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTestConfigTest extends IntegrationTestConfig {

    @Test
    void toWebResultWithList_listOfProductDto_success() {
        final String responseBody = "{\"code\":\"SUCCESS\",\"message\":null,\"data\":[{\"id\":1,\"name\":\"Product name\",\"image\":\"product-image\"}]}";
        final ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

        final WebResult<ArrayList<ProductDto>> result = toWebResultWithList(response, ProductDto.class);

        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");
        assertNull(result.getMessage(), "Response message");
        final List<Long> productIds = result.getData().stream().map(ProductDto::getId).collect(Collectors.toList());
        assertEquals(List.of(1L), productIds, "Product ids");
    }

    @Test
    void toWebResultWithList_listOfOrderDto_success() {
        final String responseBody = "{\"code\":\"SUCCESS\",\"message\":null,\"data\":[{\"id\":1,\"userId\":2,\"product\":{\"id\":3,\"name\":\"Product name\",\"image\":\"product-image\"}}]}";
        final ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

        final WebResult<ArrayList<OrderDto>> result = toWebResultWithList(response, OrderDto.class);

        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");

        final List<OrderDto> orders = result.getData();
        assertEquals(1, orders.size(), "Orders size");
        final OrderDto order = orders.get(0);
        assertNotNull(order, "Order");
        assertEquals(1L, order.getId(), "Order id");
        assertEquals(2L, order.getUserId(), "Order user id");
        assertNotNull(order.getProduct(), "Order product");
        assertEquals(3L, order.getProduct().getId(), "Order product id");
        assertEquals("Product name", order.getProduct().getName(), "Order product name");
        assertEquals("product-image", order.getProduct().getImage(), "Order product image");
    }
}
