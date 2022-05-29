package com.mws.back_end.product.interfaces;

import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.exception.MWSException;
import com.mws.back_end.product.interfaces.dto.ProductCreationDto;
import com.mws.back_end.product.interfaces.dto.ProductDto;
import com.mws.back_end.product.interfaces.dto.ProductUpdateDto;
import com.mws.back_end.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.mws.back_end.framework.dto.WebResult.newWebResult;
import static com.mws.back_end.framework.dto.WebResult.success;
import static com.mws.back_end.framework.dto.WebResultCode.ERROR_INVALID_PARAMETER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class ProductInterface {
    private static final String BASE_USER_URL = "/product";
    public static final String CREATE_PRODUCT_URL = BASE_USER_URL + "/" + "create";
    public static final String GET_PRODUCTS_URL = BASE_USER_URL + "/" + "get";
    public static final String UPDATE_PRODUCT_URL = BASE_USER_URL + "/" + "update";
    public static final String DELETE_PRODUCT_URL = BASE_USER_URL + "/" + "delete";

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


    @GetMapping(GET_PRODUCTS_URL)
    public ResponseEntity<WebResult<ArrayList<ProductDto>>> getProducts(@RequestParam Boolean active) {
        final List<ProductDto> products = productService.getProducts(active);
        return new ResponseEntity<>(success(new ArrayList<>(products)), OK);
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

    @DeleteMapping(DELETE_PRODUCT_URL)
    public ResponseEntity<WebResult<Serializable>> deleteProduct(@RequestParam long id) {
        try {
            productService.deleteProduct(id);
        } catch (MWSException e) {
            return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, e.getMessage()), BAD_REQUEST);
        }

        return new ResponseEntity<>(success(), OK);
    }

}
