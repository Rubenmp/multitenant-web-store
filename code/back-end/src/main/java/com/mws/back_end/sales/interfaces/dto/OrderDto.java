package com.mws.back_end.sales.interfaces.dto;

import com.mws.back_end.product.interfaces.dto.ProductDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class OrderDto implements Serializable {
    private Long id;
    private Long userId;
    private ProductDto product;
    private Date date;
}
