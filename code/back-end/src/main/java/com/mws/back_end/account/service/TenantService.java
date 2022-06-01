package com.mws.back_end.account.service;

import com.mws.back_end.account.interfaces.tenant.tenant.TenantDto;
import com.mws.back_end.account.interfaces.tenant.tenant.TenantUpdateDto;
import com.mws.back_end.account.interfaces.user.dto.UserRoleDto;
import com.mws.back_end.account.model.dao.TenantDao;
import com.mws.back_end.account.model.entity.Tenant;
import com.mws.back_end.account.service.security.JwtCipher;
import com.mws.back_end.framework.exception.EntityPersistenceException;
import com.mws.back_end.framework.exception.MWSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mws.back_end.account.interfaces.tenant.tenant.TenantDto.toDto;
import static com.mws.back_end.framework.utils.ExceptionUtils.requireNotNull;

@Service
public class TenantService {

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private JwtCipher jwtCipher;


    public Long createTenant(final String tenantName) throws MWSException {
        checkSuperPermissions();
        final Tenant tenant = new Tenant();
        tenant.setName(tenantName);
        try {
            return tenantDao.create(tenant).getTenantId();
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }


    public void updateTenant(final TenantUpdateDto tenantUpdateDto) throws MWSException {
        requireNotNull(tenantUpdateDto, "Tenant update info must be provided");
        checkSuperPermissions();

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

    public TenantDto getTenant(final Long tenantId) {
        return toDto(tenantDao.findWeak(tenantId));
    }

    public List<TenantDto> listTenants(final Boolean active) {
        return tenantDao.find(null, active).stream().map(TenantDto::toDto).toList();
    }

    public void deleteTenant(final long tenantId) throws MWSException {
        tenantDao.findBy("tenantId", String.valueOf(tenantId), 1);
        final Tenant tenant = tenantDao.findWeak(tenantId);
        checkSuperPermissions();

        if (tenant == null) {
            throw new MWSException("Entity not found");
        }

        tenant.setActive(false);
        tenantDao.update(tenant);
    }

    private void checkSuperPermissions() throws MWSException {
        final UserRoleDto currentUserRole = jwtCipher.getCurrentUserRole();
        if (!UserRoleDto.SUPER.equals(currentUserRole)) {
            throw new MWSException("Not allowed to handle tenants.");
        }
    }
}
