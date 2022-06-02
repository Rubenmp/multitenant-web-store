package com.mws.back_end.product.service;

import com.mws.back_end.account.interfaces.user.dto.UserRoleDto;
import com.mws.back_end.account.service.security.JwtCipher;
import com.mws.back_end.framework.exception.EntityPersistenceException;
import com.mws.back_end.framework.exception.MWSException;
import com.mws.back_end.product.interfaces.dto.ProductCreationDto;
import com.mws.back_end.product.interfaces.dto.ProductDto;
import com.mws.back_end.product.interfaces.dto.ProductUpdateDto;
import com.mws.back_end.product.model.dao.ProductDao;
import com.mws.back_end.product.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.mws.back_end.framework.utils.ExceptionUtils.require;
import static com.mws.back_end.framework.utils.ExceptionUtils.requireNotNull;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private JwtCipher jwtCipher;

    public Long createProduct(final ProductCreationDto productCreationDto) throws MWSException {
        try {
            checkProductCreation(productCreationDto);
            return productDao.create(toProduct(productCreationDto)).getId();
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }

    private void checkProductCreation(final ProductCreationDto productCreationDto) throws MWSException {
        requireNotNull(productCreationDto, "Product info must be provided");
        require(productCreationDto.getName() != null && !productCreationDto.getName().isBlank(), "Product name must be provided.");
        checkProductPermissions();
    }

    private void checkProductPermissions() throws MWSException {
        final UserRoleDto userRole = jwtCipher.getCurrentUserRole();
        if (userRole != UserRoleDto.SUPER && userRole != UserRoleDto.ADMIN) {
            throw new MWSException("Not allowed to create product(s).");
        }
    }

    private Product toProduct(final ProductCreationDto productCreationDto) throws MWSException {
        final Product product = new Product();
        final Long tenantId = jwtCipher.getCurrentTenantId();
        product.setTenantId(tenantId);
        if (tenantId == null) {
            throw new MWSException("Tenant id must be in the request context.");
        }
        product.setName(productCreationDto.getName());
        product.setImage(productCreationDto.getImage());

        return product;
    }

    public ProductDto getActiveProduct(final Long productId) {
        return getProducts(Collections.singleton(productId), true).stream().findFirst().orElse(null);
    }

    public List<ProductDto> getProducts(final Collection<Long> ids, final Boolean active) {
        return toProductDto(productDao.find(ids, active));
    }

    private List<ProductDto> toProductDto(final List<Product> products) {
        return products.stream().map(this::toProductDto).toList();
    }

    private ProductDto toProductDto(final Product product) {
        final ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setImage(product.getImage());
        return productDto;
    }

    public void updateProduct(final ProductUpdateDto productUpdateDto) throws MWSException {
        requireNotNull(productUpdateDto, "Product info must be provided.");
        require(productUpdateDto.getName() != null && !productUpdateDto.getName().isBlank(), "Product name must be provided.");
        checkProductPermissions();

        final Product productToUpdate = productDao.findWeak(productUpdateDto.getId());
        if (productToUpdate == null) {
            throw new MWSException("Entity not found");
        }
        productToUpdate.setName(productUpdateDto.getName());
        productToUpdate.setImage(productUpdateDto.getImage());

        final Product productWithSameName = productDao.findByName(productUpdateDto.getName());
        if (productWithSameName != null && !productWithSameName.getId().equals(productToUpdate.getId())) {
            throw new MWSException("Duplicated product name not allowed.");
        }

        try {
            productDao.update(productToUpdate);
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }

    public void deleteProduct(final long productId) throws MWSException {
        checkProductPermissions();

        if (productDao.findWeak(productId) == null) {
            throw new MWSException("Product not found");
        }

        productDao.delete(productId);
    }

}
