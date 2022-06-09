package com.mws.back_end.sales.service;

import com.mws.back_end.account.interfaces.user.dto.UserRoleDto;
import com.mws.back_end.account.service.security.JwtCipher;
import com.mws.back_end.framework.exception.MWSException;
import com.mws.back_end.product.interfaces.dto.ProductDto;
import com.mws.back_end.product.service.ProductService;
import com.mws.back_end.sales.interfaces.dto.OrderCreationDto;
import com.mws.back_end.sales.interfaces.dto.OrderCreationOneTransactionDto;
import com.mws.back_end.sales.interfaces.dto.OrderDto;
import com.mws.back_end.sales.model.dao.OrderDao;
import com.mws.back_end.sales.model.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        final Long userId = jwtCipher.getCurrentUserId();
        checkOrderCreationPermissions(userId);

        if (productService.getActiveProduct(orderCreationDto.getProductId()) == null) {
            throw new MWSException("Invalid product id.");
        }

        final Order order = toOrder(orderCreationDto.getProductId(), userId);
        return orderDao.createOrder(order).getId();
    }

    private void checkOrderCreationPermissions(final Long userRequested) throws MWSException {
        requireNotNull(userRequested, "User must be provided in the context.");

        final UserRoleDto userRole = jwtCipher.getCurrentUserRole();
        if (userRole == UserRoleDto.SUPER || userRole == UserRoleDto.ADMIN) {
            throw new MWSException("Not allowed to create order(s).");
        }
    }

    private Order toOrder(final Long productId, final Long userId) throws MWSException {
        final Order order = new Order();
        final Long tenantId = jwtCipher.getCurrentTenantId();
        order.setTenantId(tenantId);
        if (tenantId == null) {
            throw new MWSException("Tenant id must be in the request context.");
        }
        order.setUserId(userId);
        order.setProductId(productId);

        return order;
    }

    public List<OrderDto> listOrders(final Long userId) throws MWSException {
        checkPermissionsToListOrders(userId);

        final Long requestedUser = userId == null ? jwtCipher.getCurrentUserId() : userId;
        final List<Order> orders = orderDao.findByUser(requestedUser);
        final List<Long> productIds = orders.stream().map(Order::getProductId).toList();
        final Map<Long, ProductDto> productsMap =
                productService.getProducts(productIds, null).stream().collect(Collectors.toMap(ProductDto::getId, p -> p, (prev, newP) -> prev));

        return orders.stream().map(o -> toDto(o, productsMap)).filter(o -> o.getProduct() != null).toList();
    }

    private void checkPermissionsToListOrders(final Long requestedUserId) throws MWSException {
        final UserRoleDto currentUserRole = jwtCipher.getCurrentUserRole();
        if (currentUserRole == null ||
                (currentUserRole == UserRoleDto.USER && requestedUserId != null &&
                        !Objects.equals(requestedUserId, jwtCipher.getCurrentUserId()))) {
            throw new MWSException("Not allowed to list order(s) from other user.");
        }
    }

    public OrderDto toDto(final Order order, final Map<Long, ProductDto> productsMap) {
        final OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setUserId(order.getUserId());
        orderDto.setProduct(productsMap.get(order.getProductId()));
        orderDto.setDate(order.getDate());

        return orderDto;
    }

    public Long createOrderInOneTransaction(final OrderCreationOneTransactionDto orderCreationDto) {
        final Order order = new Order();
        order.setTenantId(orderCreationDto.getTenantId());
        order.setUserId(orderCreationDto.getUserId());
        order.setProductId(orderCreationDto.getProductId());

        return orderDao.createOrder(order).getId();
    }
}
