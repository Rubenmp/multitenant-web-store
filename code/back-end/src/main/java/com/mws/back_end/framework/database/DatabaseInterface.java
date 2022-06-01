package com.mws.back_end.framework.database;

import com.mws.back_end.account.interfaces.tenant.TenantInterface;
import com.mws.back_end.account.interfaces.user.UserInterface;
import com.mws.back_end.account.interfaces.user.dto.UserCreationDto;
import com.mws.back_end.account.interfaces.user.dto.UserRoleDto;
import com.mws.back_end.framework.database.dto.DatabaseFillDto;
import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.dto.WebResultCode;
import com.mws.back_end.framework.exception.MWSRException;
import com.mws.back_end.product.interfaces.ProductInterface;
import com.mws.back_end.product.interfaces.dto.ProductCreationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.LongStream;

import static com.mws.back_end.framework.dto.WebResult.newWebResult;
import static com.mws.back_end.framework.dto.WebResult.success;
import static com.mws.back_end.framework.dto.WebResultCode.ERROR_INVALID_PARAMETER;
import static com.mws.back_end.framework.utils.ExceptionUtils.require;
import static com.mws.back_end.framework.utils.ExceptionUtils.requireNotNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class DatabaseInterface {
    public static final String BASE_TENANT_URL = "/database";
    public static final String FILL_DATABASE_URL = BASE_TENANT_URL + "/" + "fill";

    @Autowired
    private TenantInterface tenantInterface;

    @Autowired
    private UserInterface userInterface;

    @Autowired
    private ProductInterface productInterface;

    @PostMapping(FILL_DATABASE_URL)
    public ResponseEntity<WebResult<Serializable>> fillDatabase(@RequestBody DatabaseFillDto databaseFillDto) {
        requireNotNull(databaseFillDto, "Database fill dto required");

        try {
            checkLimits(databaseFillDto);
            final List<Long> tenantIds = createTenants(databaseFillDto.getNumberOfTenants());
            createUsers(tenantIds, databaseFillDto.getUsersPerTenant());
            createProducts(databaseFillDto.getNumberOfProducts());
        } catch (MWSRException e) {
            return new ResponseEntity<>(newWebResult(ERROR_INVALID_PARAMETER, e.getMessage()), BAD_REQUEST);
        }

        return new ResponseEntity<>(success(), OK);
    }

    private void createProducts(final Long numberOfProducts) {
        LongStream.range(0, numberOfProducts).forEach(productIndex -> {
            ResponseEntity<WebResult<Long>> response = productInterface.createProduct(newProductCreationDto(productIndex));
            checkOkResponse(response, "It was not possible to create product [product_number=" + productIndex + "]");
        });
    }

    private ProductCreationDto newProductCreationDto(long productIndex) {
        final ProductCreationDto productCreationDto = new ProductCreationDto();
        productCreationDto.setName("[auto] Product " + (productIndex + 1));
        productCreationDto.setImage("image");
        return productCreationDto;
    }

    private void checkLimits(final DatabaseFillDto databaseFillDto) {
        final Long numberOfTenants = databaseFillDto.getNumberOfTenants();
        requireNotNull(numberOfTenants, "Number of tenants must be provided");
        require(0 < numberOfTenants && numberOfTenants < 100, "Number of tenants is limited ]0, 100]");

        final Long usersPerTenant = databaseFillDto.getUsersPerTenant();
        requireNotNull(usersPerTenant, "Number of users per tenant must be provided");
        require(0 <= usersPerTenant && usersPerTenant <= 1000, "Number of users per tenant is limited [0, 1.000]");

        final Long numberOfProducts = databaseFillDto.getNumberOfProducts();
        requireNotNull(numberOfProducts, "Number of products per tenant must be provided");
        require(0 <= numberOfProducts && numberOfProducts <= 1000, "Number of products per tenant is limited [0, 1.000]");
    }

    private void createUsers(final List<Long> tenantIds, final Long usersPerTenant) {
        tenantIds.forEach(tenantId -> LongStream.range(0, usersPerTenant).forEach(userIndex -> {
            ResponseEntity<WebResult<Long>> response = userInterface.createUser(newUserCreationDto(tenantId, userIndex));
            checkOkResponse(response, "It was not possible to create user [tenant_id=" + tenantId + ", user_number=" + userIndex + "]");
        }));
    }

    private UserCreationDto newUserCreationDto(final Long tenantId, final long userIndex) {
        final UserCreationDto creationDto = new UserCreationDto();
        creationDto.setTenantId(tenantId);
        final UserRoleDto role;
        if (userIndex == 0) {
            role = UserRoleDto.SUPER;
        } else if ((userIndex - 1) % 20 == 0) {
            role = UserRoleDto.ADMIN;
        } else {
            role = UserRoleDto.USER;
        }
        creationDto.setRole(role);
        creationDto.setEmail("user" + (userIndex + 1) + "tenant" + tenantId + "@mwstest.com");
        creationDto.setPassword("Password1");
        creationDto.setFirstName("First name " + (userIndex + 1));
        creationDto.setLastName("Last name " + (userIndex + 1));
        return creationDto;
    }

    private List<Long> createTenants(final Long numberOfTenants) {
        return LongStream.range(0, numberOfTenants).map(index -> {
            ResponseEntity<WebResult<Long>> response = tenantInterface.createTenant("Tenant " + (index + 1));
            checkOkResponse(response, "It was not possible to create tenant");
            return Objects.requireNonNull(response.getBody()).getData();
        }).boxed().toList();
    }

    private void checkOkResponse(final ResponseEntity<WebResult<Long>> response, final String errorMessage) {
        final WebResult<Long> body = response.getBody();
        final WebResultCode responseCode = body != null ? body.getCode() : null;
        if (WebResultCode.SUCCESS != responseCode) {
            throw new MWSRException(errorMessage);
        }
    }

}
