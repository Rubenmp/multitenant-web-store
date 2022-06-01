package com.mws.back_end.sales.interfaces;


import com.mws.back_end.framework.IntegrationTestConfig;
import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.dto.WebResultCode;
import com.mws.back_end.sales.interfaces.dto.OrderCreationDto;
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
import java.util.List;

import static com.mws.back_end.framework.IntegrationTestConfig.TEST_PROFILE;
import static com.mws.back_end.sales.interfaces.OrderInterface.CREATE_ORDER_URL;
import static com.mws.back_end.sales.interfaces.OrderInterface.LIST_ORDERS_URL;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderInterfaceIT extends IntegrationTestConfig {

    @Test
    void createOrder_thenListThem_success() {
        final OrderCreationDto creationDto = new OrderCreationDto();
        creationDto.setUserId(USER_ID);
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
                null,
                String.class);

        assertEquals(HttpStatus.OK, listResponse.getStatusCode(), "Response status");
        final WebResult<ArrayList<OrderDto>> result = toWebResultWithList(listResponse, OrderDto.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");

        final List<OrderDto> orders = result.getData();
        assertTrue(orders != null && !orders.isEmpty(), "Orders not empty");
        final OrderDto returnedOrder = orders.stream().filter(o -> orderId.equals(o.getId())).findFirst().orElse(null);
        assertNotNull(returnedOrder, "Created order must be listed");
        assertEquals(creationDto.getUserId(), returnedOrder.getUserId(), "Order user id");
        assertEquals(creationDto.getProductId(), returnedOrder.getProductId(), "Order product id");
    }

    private Long checkOrderWasCreated(ResponseEntity<String> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final WebResult<Long> result = toWebResult(response, Long.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");
        assertNotNull(result.getData(), "Order id");

        return result.getData();
    }

}
