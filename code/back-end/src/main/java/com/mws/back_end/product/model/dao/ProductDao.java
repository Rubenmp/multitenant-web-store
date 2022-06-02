package com.mws.back_end.product.model.dao;

import com.mws.back_end.framework.database.GenericDaoImpl;
import com.mws.back_end.product.model.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static com.mws.back_end.product.model.entity.Product.PRODUCT_COLUMN_NAME;

@Component
public class ProductDao extends GenericDaoImpl<Product, Long> {

    public Product findByName(final String name) {
        return findBy(PRODUCT_COLUMN_NAME, name, 1).stream().findFirst().orElse(null);
    }

    public List<Product> find(final Collection<Long> ids, final Boolean active) {
        final DBSearch dbSearch = new DBSearch();
        dbSearch.setIds(ids);
        dbSearch.setActive(active);
        return find(dbSearch);
    }
}

