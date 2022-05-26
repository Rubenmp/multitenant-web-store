package com.mws.back_end.product.interfaces.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductCreationDto {
    private String name;
    private String image;
}
