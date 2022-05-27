package com.mws.back_end.product.service;

import com.mws.back_end.framework.exception.EntityPersistenceException;
import com.mws.back_end.framework.exception.MWSException;
import com.mws.back_end.product.interfaces.dto.ProductCreationDto;
import com.mws.back_end.product.interfaces.dto.ProductDto;
import com.mws.back_end.product.interfaces.dto.ProductUpdateDto;
import com.mws.back_end.product.model.dao.ProductDao;
import com.mws.back_end.product.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<ProductDto> getProducts(final Boolean active) {
        return toProductDto(productDao.find(null, active));
    }

    private List<ProductDto> toProductDto(List<Product> products) {
        return products.stream().map(this::toProductDto).collect(Collectors.toList());
    }

    private ProductDto toProductDto(Product product) {
        final ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setImage(product.getImage());
        return productDto;
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


    public void deleteProduct(final long productId) throws MWSException {
        final Product product = productDao.findWeak(productId);
        if (product == null) {
            throw new MWSException("Product not found");
        }

        product.setActive(false);
        productDao.update(product);
    }

}
