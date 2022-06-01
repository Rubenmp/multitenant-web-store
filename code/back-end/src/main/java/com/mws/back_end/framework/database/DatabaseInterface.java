package com.mws.back_end.framework.database;

import com.mws.back_end.account.service.TenantService;
import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.exception.MWSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.mws.back_end.framework.dto.WebResult.newWebResult;
import static com.mws.back_end.framework.dto.WebResult.success;
import static com.mws.back_end.framework.dto.WebResultCode.ERROR_INVALID_PARAMETER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class DatabaseInterface {
    public static final String BASE_TENANT_URL = "/database";
    public static final String FILL_DATABASE_URL = BASE_TENANT_URL + "/" + "fill";

    @Autowired
    private TenantService tenantService;

    @PostMapping(FILL_DATABASE_URL)
    public ResponseEntity<WebResult<Long>> fillDatabase(@RequestParam String name) {
        final Long tenantId;
        try {
            tenantId = tenantService.createTenant(name);
        } catch (MWSException e) {
            return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, e.getMessage()), BAD_REQUEST);
        }

        return new ResponseEntity<>(success(tenantId), OK);
    }

}
