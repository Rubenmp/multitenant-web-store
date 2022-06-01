package com.mws.back_end.sales.model.dao;

import com.mws.back_end.framework.database.GenericDaoImpl;
import com.mws.back_end.sales.model.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.mws.back_end.sales.model.entity.Order.ORDER_COLUMN_USER_ID;

@Component
public class OrderDao extends GenericDaoImpl<Order, Long> {

    public List<Order> findByUser(final Long userId) {
        return findBy(ORDER_COLUMN_USER_ID, String.valueOf(userId), null);
    }
}

