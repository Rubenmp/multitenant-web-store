package com.mws.back_end.sales.service;

import com.mws.back_end.account.service.security.JwtCipher;
import com.mws.back_end.framework.exception.MWSException;
import com.mws.back_end.product.interfaces.dto.ProductDto;
import com.mws.back_end.product.service.ProductService;
import com.mws.back_end.sales.interfaces.dto.OrderCreationDto;
import com.mws.back_end.sales.interfaces.dto.OrderDto;
import com.mws.back_end.sales.model.dao.OrderDao;
import com.mws.back_end.sales.model.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<OrderDto> listOrders(final long userId) {
        final List<Order> orders = orderDao.findByUser(userId);
        final List<Long> productIds = orders.stream().map(Order::getProductId).toList();
        final Map<Long, ProductDto> productsMap =
                productService.getProducts(productIds, null).stream().collect(Collectors.toMap(ProductDto::getId, p -> p, (prev, newP) -> prev));

        return orders.stream().map(o -> toDto(o, productsMap)).toList();
    }

    public OrderDto toDto(final Order order, final Map<Long, ProductDto> productsMap) {
        final OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setUserId(order.getUserId());
        orderDto.setProduct(productsMap.get(order.getProductId()));

        return orderDto;
    }
}
