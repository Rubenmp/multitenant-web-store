package com.mws.back_end.sales.interfaces;


import com.mws.back_end.framework.IntegrationTestConfig;
import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.dto.WebResultCode;
import com.mws.back_end.sales.interfaces.dto.OrderCreationDto;
import com.mws.back_end.sales.interfaces.dto.OrderCreationOneTransactionDto;
import com.mws.back_end.sales.interfaces.dto.OrderDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mws.back_end.framework.IntegrationTestConfig.TEST_PROFILE;
import static com.mws.back_end.sales.interfaces.OrderInterface.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderInterfaceIT extends IntegrationTestConfig {

    @Test
    void createOrder_thenListThem_success() {
        final OrderCreationDto creationDto = new OrderCreationDto();
        creationDto.setProductId(PRODUCT_ID);
        final HttpEntity<String> createHttpEntity = createUserHttpEntity(toJson(creationDto));
        final URI createUri = getUri(CREATE_ORDER_URL);

        final ResponseEntity<String> createResponse = restTemplate.exchange(
                createUri,
                HttpMethod.POST,
                createHttpEntity,
                String.class);

        final Long orderId = checkOrderWasCreated(createResponse);

        final URI listUri = getUri(LIST_ORDERS_URL, Pair.of("userId", String.valueOf(USER_ID)));
        final ResponseEntity<String> listResponse = restTemplate.exchange(
                listUri,
                HttpMethod.GET,
                createUserHttpEntity(),
                String.class);

        assertEquals(HttpStatus.OK, listResponse.getStatusCode(), "Response status");
        final WebResult<ArrayList<OrderDto>> result = toWebResultWithList(listResponse, OrderDto.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");

        final List<OrderDto> orders = result.getData();
        assertTrue(orders != null && !orders.isEmpty(), "Orders not empty");
        final OrderDto returnedOrder = orders.stream().filter(o -> orderId.equals(o.getId())).findFirst().orElse(null);
        assertNotNull(returnedOrder, "Created order must be listed");
        assertEquals(USER_ID, returnedOrder.getUserId(), "Order user id must be the logged user.");
        assertNotNull(returnedOrder.getDate(), "Order date must be not null.");
        assertFalse(returnedOrder.getDate().after(new Date()), "Order date must be before now.");
        assertNotNull(returnedOrder.getProduct(), "Order product");
        assertEquals(creationDto.getProductId(), returnedOrder.getProduct().getId(), "Order product id");
    }

    @Test
    void createOrderInOneTransaction_success() {
        final OrderCreationOneTransactionDto creationDto = createOrderCreationOneTransactionDto();
        final URI createUri = getUri(CREATE_ORDER_ONE_TRANSACTION_URL);

        final ResponseEntity<String> createResponse = restTemplate.exchange(
                createUri,
                HttpMethod.POST,
                createHttpEntityInternal(toJson(creationDto)),
                String.class);

        checkOrderWasCreated(createResponse);
    }

    private OrderCreationOneTransactionDto createOrderCreationOneTransactionDto() {
        final OrderCreationOneTransactionDto creationDto = new OrderCreationOneTransactionDto();
        creationDto.setTenantId(TENANT_ID);
        creationDto.setUserId(USER_ID);
        creationDto.setProductId(PRODUCT_ID);
        return creationDto;
    }

    private Long checkOrderWasCreated(ResponseEntity<String> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final WebResult<Long> result = toWebResult(response, Long.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");
        assertNotNull(result.getData(), "Order id");

        return result.getData();
    }


    @Test
    void listOrders_fromOtherUser_notAllowed() {
        final ResponseEntity<String> response = restTemplate.exchange(
                getUri(LIST_ORDERS_URL, Pair.of("userId", "4")),
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response status");
        final WebResult<ArrayList<OrderDto>> result = toWebResultWithList(response, OrderDto.class);
        assertEquals(WebResultCode.ERROR_INVALID_PARAMETER, result.getCode(), "Result code");
    }
}
