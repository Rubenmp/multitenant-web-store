package com.mws.back_end.product.interfaces.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductUpdateDto extends ProductCreationDto {
    private Long id;
}
