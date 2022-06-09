package com.mws.back_end.sales.interfaces.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderCreationOneTransactionDto {
    private Long tenantId;
    private Long userId;
    private Long productId;
}
