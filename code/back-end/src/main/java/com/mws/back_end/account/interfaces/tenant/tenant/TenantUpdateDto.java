package com.mws.back_end.account.interfaces.tenant.tenant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TenantUpdateDto {
    private Long id;
    private String name;
}
