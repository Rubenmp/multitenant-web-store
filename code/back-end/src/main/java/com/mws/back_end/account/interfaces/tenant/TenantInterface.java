package com.mws.back_end.account.interfaces.tenant;

import com.mws.back_end.account.interfaces.tenant.tenant.TenantDto;
import com.mws.back_end.account.interfaces.tenant.tenant.TenantUpdateDto;
import com.mws.back_end.account.service.TenantService;
import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.exception.MWSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.mws.back_end.framework.dto.WebResult.newWebResult;
import static com.mws.back_end.framework.dto.WebResult.success;
import static com.mws.back_end.framework.dto.WebResultCode.ERROR_INVALID_PARAMETER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class TenantInterface {
    private static final String BASE_TENANT_URL = "/tenant";
    public static final String CREATE_TENANT_URL = BASE_TENANT_URL + "/" + "create";
    public static final String UPDATE_TENANT_URL = BASE_TENANT_URL + "/" + "update";
    public static final String LIST_TENANTS_URL = BASE_TENANT_URL + "/" + "list";
    public static final String DELETE_TENANT_URL = BASE_TENANT_URL + "/" + "delete";

    @Autowired
    private TenantService tenantService;

    @PostMapping(CREATE_TENANT_URL)
    public ResponseEntity<WebResult<Long>> createTenant(@RequestParam String name) {
        final Long tenantId;
        try {
            tenantId = tenantService.createTenant(name);
        } catch (MWSException e) {
            return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, e.getMessage()), BAD_REQUEST);
        }

        return new ResponseEntity<>(success(tenantId), OK);
    }

    @PutMapping(UPDATE_TENANT_URL)
    public ResponseEntity<WebResult<Serializable>> updateTenant(@RequestBody TenantUpdateDto tenantUpdateDto) {
        try {
            tenantService.updateTenant(tenantUpdateDto);
        } catch (MWSException e) {
            return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, e.getMessage()), BAD_REQUEST);
        }

        return new ResponseEntity<>(success(), OK);
    }

    @GetMapping(LIST_TENANTS_URL)
    public ResponseEntity<WebResult<ArrayList<TenantDto>>> listTenants(@RequestParam Boolean active) {
        List<TenantDto> tenants = tenantService.listTenants(active);

        if (tenants != null) {
            return new ResponseEntity<>(success(new ArrayList<>(tenants)), OK);
        }

        return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, "Invalid user id"), BAD_REQUEST);
    }

    @DeleteMapping(DELETE_TENANT_URL)
    public ResponseEntity<WebResult<Serializable>> deleteTenant(@RequestParam Long id) {
        try {
            tenantService.deleteTenant(id);
        } catch (MWSException e) {
            return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, "Invalid id"), BAD_REQUEST);
        }

        return new ResponseEntity<>(success(), OK);
    }

}