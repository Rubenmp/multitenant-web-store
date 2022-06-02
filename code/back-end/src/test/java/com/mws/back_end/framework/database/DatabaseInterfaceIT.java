package com.mws.back_end.framework.database;


import com.mws.back_end.framework.IntegrationTestConfig;
import com.mws.back_end.framework.database.dto.DatabaseFillDto;
import com.mws.back_end.framework.dto.WebResult;
import com.mws.back_end.framework.dto.WebResultCode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static com.mws.back_end.framework.IntegrationTestConfig.TEST_PROFILE;
import static com.mws.back_end.framework.database.DatabaseInterface.FILL_DATABASE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DatabaseInterfaceIT extends IntegrationTestConfig {

    @Test
    void fillDatabase_happyPath_success() {
        final DatabaseFillDto fillDto = getDatabaseFillDto();

        final ResponseEntity<String> response = restTemplate.exchange(
                getUri(FILL_DATABASE_URL),
                HttpMethod.POST,
                createSuperHttpEntity(toJson(fillDto)),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status");
        final WebResult<Long> result = toWebResult(response, Long.class);
        assertEquals(WebResultCode.SUCCESS, result.getCode(), "Result code");
    }

    private DatabaseFillDto getDatabaseFillDto() {
        final DatabaseFillDto databaseFillDto = new DatabaseFillDto();
        databaseFillDto.setNumberOfTenants(2L);
        databaseFillDto.setUsersPerTenant(10L);
        databaseFillDto.setNumberOfProducts(5L);

        return databaseFillDto;
    }

}
