package com.mws.back_end.sales.service;

import com.mws.back_end.account.service.security.JwtProvider;
import com.mws.back_end.framework.exception.MWSException;
import com.mws.back_end.sales.interfaces.dto.OrderCreationDto;
import com.mws.back_end.sales.interfaces.dto.OrderDto;
import com.mws.back_end.sales.model.dao.OrderDao;
import com.mws.back_end.sales.model.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mws.back_end.framework.utils.ExceptionUtils.requireNotNull;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private JwtProvider jwtProvider;


    public Long createOrder(final OrderCreationDto orderCreationDto) throws MWSException {
        requireNotNull(orderCreationDto, "Order info must be provided");
        requireNotNull(orderCreationDto.getProductId(), "Product must be provided");
        requireNotNull(orderCreationDto.getUserId(), "User must be provided");

        final Order order = toOrder(orderCreationDto);
        return orderDao.create(order).getId();
    }

    private Order toOrder(OrderCreationDto orderCreationDto) throws MWSException {
        final Order order = new Order();
        final Long tenantId = jwtProvider.getTenantIdFromRequest();
        order.setTenantId(tenantId);
        if (tenantId == null) {
            throw new MWSException("Tenant id must be in the request context.");
        }
        order.setTenantId(orderCreationDto.getUserId());
        order.setUserId(orderCreationDto.getUserId());
        order.setProductId(orderCreationDto.getProductId());

        return order;
    }

    public List<OrderDto> listOrders(Long userId) {
        final List<Order> orders = orderDao.findByUser(userId);
        return orders.stream().map(Order::toDto).toList();
    }

}
