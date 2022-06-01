package com.mws.back_end.framework.database.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DatabaseFillDto {
    private Long numberOfTenants;
    private Long usersPerTenant;
    private Long productsPerTenant;
}
