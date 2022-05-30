package com.mws.back_end.account.service;

import com.mws.back_end.account.interfaces.tenant.tenant.TenantDto;
import com.mws.back_end.account.interfaces.tenant.tenant.TenantUpdateDto;
import com.mws.back_end.account.model.dao.TenantDao;
import com.mws.back_end.account.model.entity.Tenant;
import com.mws.back_end.framework.exception.EntityPersistenceException;
import com.mws.back_end.framework.exception.MWSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    @Autowired
    private TenantDao tenantDao;


    public Long createTenant(final String tenantName) throws MWSException {
        final Tenant tenant = new Tenant();
        tenant.setName(tenantName);
        try {
            return tenantDao.create(tenant).getId();
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }


    public void updateTenant(final TenantUpdateDto tenantUpdateDto) throws MWSException {
        final Tenant tenant = tenantDao.findWeak(tenantUpdateDto.getId());

        if (tenant == null) {
            throw new MWSException("Entity not found");
        }

        if (tenant.getName().equals(tenantUpdateDto.getName())) {
            return;
        }
        if (tenantDao.findByName(tenantUpdateDto.getName()) != null) {
            throw new MWSException("Duplicated email");
        }

        tenant.setName(tenantUpdateDto.getName());

        try {
            tenantDao.update(tenant);
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }


    public List<TenantDto> listTenants(final Boolean active) {
        return tenantDao.find(null, active).stream().map(TenantDto::toDto).toList();
    }

    public void deleteTenant(Long tenantId) throws MWSException {
        final Tenant tenant = tenantDao.findWeak(tenantId);

        if (tenant == null) {
            throw new MWSException("Entity not found");
        }

        tenant.setActive(false);
        tenantDao.update(tenant);
    }
}
