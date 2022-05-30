package com.mws.back_end.account.model.dao;

import com.mws.back_end.account.model.entity.Tenant;
import com.mws.back_end.framework.database.GenericDaoImpl;
import org.springframework.stereotype.Component;

import static com.mws.back_end.account.model.entity.Tenant.TENANT_COLUMN_NAME;

@Component
public class TenantDao extends GenericDaoImpl<Tenant, Long> {

    public Tenant findByName(final String name) {
        return findBy(TENANT_COLUMN_NAME, name, 1).stream().findFirst().orElse(null);
    }
}

