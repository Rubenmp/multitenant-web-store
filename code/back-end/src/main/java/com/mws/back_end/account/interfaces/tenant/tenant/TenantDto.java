package com.mws.back_end.account.interfaces.tenant.tenant;

import com.mws.back_end.account.model.entity.Tenant;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class TenantDto implements Serializable {
    private Long id;

    @NotNull
    private String name;
    private boolean active;

    public static TenantDto toDto(Tenant entity) {
        final TenantDto tenant = new TenantDto();
        tenant.setId(entity.getId());
        tenant.setName(entity.getName());
        tenant.setActive(entity.isActive());

        return tenant;
    }
}
