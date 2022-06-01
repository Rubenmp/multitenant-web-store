package com.mws.back_end.sales.interfaces.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class OrderDto implements Serializable {
    private Long id;
    private Long userId;
    private Long productId;
}
