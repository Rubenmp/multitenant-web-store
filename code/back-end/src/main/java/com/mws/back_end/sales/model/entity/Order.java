package com.mws.back_end.sales.model.entity;


import com.mws.back_end.sales.interfaces.dto.OrderDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import static com.mws.back_end.sales.model.entity.Order.ORDER_TABLE;
import static javax.persistence.GenerationType.IDENTITY;


@Data
@Entity
@NoArgsConstructor
@Table(name = ORDER_TABLE)
public class Order {
    protected static final String ORDER_TABLE = "order_table";
    public static final String ORDER_COLUMN_USER_ID = "userId";

    @NotNull
    private Long tenantId;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    private Long userId;
    @NotNull
    private Long productId;

    public static OrderDto toDto(final Order order) {
        final OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setUserId(order.getUserId());
        orderDto.setProductId(order.getProductId());

        return orderDto;
    }
}
