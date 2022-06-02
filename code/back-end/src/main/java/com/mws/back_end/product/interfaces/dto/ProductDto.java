package com.mws.back_end.product.interfaces.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ProductDto implements Serializable {
    private Long id;
    private String name;
    private String image;
    private String description;
}
