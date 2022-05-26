package com.mws.back_end.product.service;

import com.mws.back_end.framework.exception.EntityPersistenceException;
import com.mws.back_end.framework.exception.MWSException;
import com.mws.back_end.product.interfaces.dto.ProductCreationDto;
import com.mws.back_end.product.interfaces.dto.ProductUpdateDto;
import com.mws.back_end.product.model.dao.ProductDao;
import com.mws.back_end.product.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    public Long createProduct(final ProductCreationDto productCreationDto) throws MWSException {
        try {
            return productDao.create(toProduct(productCreationDto)).getId();
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }

    private Product toProduct(ProductCreationDto productCreationDto) {
        final Product product = new Product();
        product.setName(productCreationDto.getName());
        product.setImage(productCreationDto.getImage());

        return product;
    }

    public void updateProduct(final ProductUpdateDto productUpdateDto) throws MWSException {
        final Product product = toProduct(productUpdateDto);

        if (productDao.findWeak(product.getId()) == null) {
            throw new MWSException("Entity not found");
        }

        if (!productDao.findByName(productUpdateDto.getName()).isEmpty()) {
            throw new MWSException("Duplicated email");
        }

        try {
            productDao.update(product);
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }

    private Product toProduct(ProductUpdateDto productUpdateDto) {
        final Product product = new Product();
        product.setId(productUpdateDto.getId());
        product.setName(productUpdateDto.getName());
        product.setImage(productUpdateDto.getImage());

        return product;
    }

}
