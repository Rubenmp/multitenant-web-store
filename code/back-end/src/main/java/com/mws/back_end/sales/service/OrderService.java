package com.mws.back_end.sales.service;

import com.mws.back_end.account.service.security.JwtCipher;
import com.mws.back_end.framework.exception.MWSException;
import com.mws.back_end.product.service.ProductService;
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
    private JwtCipher jwtCipher;

    @Autowired
    private ProductService productService;


    public Long createOrder(final OrderCreationDto orderCreationDto) throws MWSException {
        requireNotNull(orderCreationDto, "Order info must be provided");
        requireNotNull(orderCreationDto.getProductId(), "Product must be provided");
        requireNotNull(orderCreationDto.getUserId(), "User must be provided");
        if (productService.getActiveProduct(orderCreationDto.getProductId()) == null) {
            throw new MWSException("Invalid product id.");
        }

        final Order order = toOrder(orderCreationDto);
        return orderDao.create(order).getId();
    }

    private Order toOrder(final OrderCreationDto orderCreationDto) throws MWSException {
        final Order order = new Order();
        final Long tenantId = jwtCipher.getCurrentTenantId();
        order.setTenantId(tenantId);
        if (tenantId == null) {
            throw new MWSException("Tenant id must be in the request context.");
        }
        order.setTenantId(orderCreationDto.getUserId());
        order.setUserId(orderCreationDto.getUserId());
        order.setProductId(orderCreationDto.getProductId());

        return order;
    }

    public List<OrderDto> listOrders(final Long userId) {
        final List<Order> orders = orderDao.findByUser(userId);
        return orders.stream().map(Order::toDto).toList();
    }

}
