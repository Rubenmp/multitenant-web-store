package com.mws.back_end.account.interfaces.tenant;

import com.mws.back_end.account.interfaces.tenant.tenant.TenantCreationDto;
import com.mws.back_end.account.interfaces.tenant.tenant.TenantDto;
import com.mws.back_end.account.interfaces.tenant.tenant.TenantUpdateDto;
import com.mws.back_end.account.service.TenantService;
import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.exception.MWSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public static final String BASE_TENANT_URL = "/tenant";
    public static final String CREATE_TENANT_URL = BASE_TENANT_URL + "/" + "create";
    public static final String UPDATE_TENANT_URL = BASE_TENANT_URL + "/" + "update";
    public static final String LIST_TENANTS_URL = BASE_TENANT_URL + "/" + "list";
    public static final String DELETE_TENANT_URL = BASE_TENANT_URL + "/" + "delete";

    @Autowired
    private TenantService tenantService;

    @PostMapping(CREATE_TENANT_URL)
    public ResponseEntity<WebResult<Long>> createTenant(@RequestBody TenantCreationDto tenantData) {
        try {
            final Long tenantId = tenantService.createTenant(tenantData.getName());
            return new ResponseEntity<>(success(tenantId), OK);
        } catch (MWSException e) {
            return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, e.getMessage()), BAD_REQUEST);
        }
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
    public ResponseEntity<WebResult<ArrayList<TenantDto>>> listTenants(@RequestParam(required = false) Boolean active) {
        List<TenantDto> tenants = tenantService.listTenants(active);

        if (tenants != null) {
            return new ResponseEntity<>(success(new ArrayList<>(tenants)), OK);
        }

        return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, "Invalid tenants"), BAD_REQUEST);
    }

    @DeleteMapping(DELETE_TENANT_URL)
    public ResponseEntity<WebResult<Serializable>> deleteTenant(@RequestParam long id) {
        try {
            tenantService.deleteTenant(id);
        } catch (MWSException e) {
            return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, e.getMessage()), BAD_REQUEST);
        }

        return new ResponseEntity<>(success(), OK);
    }

}
