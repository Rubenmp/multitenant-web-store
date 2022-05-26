package com.mws.back_end.product.interfaces;

import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.exception.MWSException;
import com.mws.back_end.product.interfaces.dto.ProductCreationDto;
import com.mws.back_end.product.interfaces.dto.ProductUpdateDto;
import com.mws.back_end.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

import static com.mws.back_end.framework.dto.WebResult.newWebResult;
import static com.mws.back_end.framework.dto.WebResult.success;
import static com.mws.back_end.framework.dto.WebResultCode.ERROR_INVALID_PARAMETER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class ProductInterface {
    private static final String BASE_USER_URL = "/product";
    public static final String CREATE_PRODUCT_URL = BASE_USER_URL + "/" + "create";
    public static final String UPDATE_PRODUCT_URL = BASE_USER_URL + "/" + "update";

    @Autowired
    private ProductService productService;

    @PostMapping(CREATE_PRODUCT_URL)
    public ResponseEntity<WebResult<Long>> createProduct(@RequestBody ProductCreationDto productCreationDto) {
        final Long productId;
        try {
            productId = productService.createProduct(productCreationDto);
        } catch (MWSException e) {
            return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, e.getMessage()), BAD_REQUEST);
        }

        return new ResponseEntity<>(success(productId), OK);
    }

    @PutMapping(UPDATE_PRODUCT_URL)
    public ResponseEntity<WebResult<Serializable>> updateProduct(@RequestBody ProductUpdateDto productUpdateDto) {
        try {
            productService.updateProduct(productUpdateDto);
        } catch (MWSException e) {
            return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, e.getMessage()), BAD_REQUEST);
        }

        return new ResponseEntity<>(success(), OK);
    }

}
