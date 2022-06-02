package com.mws.back_end.sales.interfaces;

import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.exception.MWSException;
import com.mws.back_end.sales.interfaces.dto.OrderCreationDto;
import com.mws.back_end.sales.interfaces.dto.OrderDto;
import com.mws.back_end.sales.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.mws.back_end.framework.dto.WebResult.newWebResult;
import static com.mws.back_end.framework.dto.WebResult.success;
import static com.mws.back_end.framework.dto.WebResultCode.ERROR_INVALID_PARAMETER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class OrderInterface {
    private static final String BASE_ORDER_URL = "/order";
    public static final String CREATE_ORDER_URL = BASE_ORDER_URL + "/" + "create";
    public static final String LIST_ORDERS_URL = BASE_ORDER_URL + "/" + "list";

    @Autowired
    private OrderService orderService;

    @PostMapping(CREATE_ORDER_URL)
    public ResponseEntity<WebResult<Long>> createOrder(@RequestBody OrderCreationDto orderCreationDto) {
        final Long orderId;
        try {
            orderId = orderService.createOrder(orderCreationDto);
        } catch (MWSException e) {
            return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, e.getMessage()), BAD_REQUEST);
        }

        return new ResponseEntity<>(success(orderId), OK);
    }


    @GetMapping(LIST_ORDERS_URL)
    public ResponseEntity<WebResult<ArrayList<OrderDto>>> listOrders(@RequestParam long userId) {
        final List<OrderDto> orders;
        try {
            orders = orderService.listOrders(userId);
        } catch (MWSException e) {
            return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, e.getMessage()), BAD_REQUEST);
        }

        return new ResponseEntity<>(success(new ArrayList<>(orders)), OK);
    }

}
