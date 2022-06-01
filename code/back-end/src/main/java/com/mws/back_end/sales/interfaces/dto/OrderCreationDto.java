package com.mws.back_end.sales.interfaces.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderCreationDto {
    private Long userId;
    private Long productId;
}
