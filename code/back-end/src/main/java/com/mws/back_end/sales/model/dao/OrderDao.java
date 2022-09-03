package com.mws.back_end.sales.model.dao;

import com.mws.back_end.framework.database.GenericDaoImpl;
import com.mws.back_end.framework.exception.EntityPersistenceException;
import com.mws.back_end.sales.model.entity.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.mws.back_end.sales.model.entity.Order.ORDER_COLUMN_USER_ID;

@Component
public class OrderDao extends GenericDaoImpl<Order, Long> {

    @Transactional
    public Order createOrder(final Order order) throws EntityPersistenceException {
        order.setDate(new Date());

        this.entityManager.persist(order);

        return order;
    }

    public List<Order> findByUser(final Long userId) {
        if (userId == null) {
            return null;
        }
        return findBy(ORDER_COLUMN_USER_ID, String.valueOf(userId), null);
    }

    public List<Order> findAll() {
        DBSearch search = new DBSearch();
        return find(search);
    }
}

