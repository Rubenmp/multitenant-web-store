package com.mws.backend.framework.database;

import com.mws.backend.framework.utils.StringUtils;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
class DatabaseConfig {

    @Bean
    @Profile("!test")
    public DataSource getDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url(getDataSourceUrl(false))
                .username(getDatabaseUsername())
                .password(getDatabasePasswordForUser())
                .build();
    }

    private String getDataSourceUrl(final boolean testProfile) {
        return "jdbc:mysql://"
                + getDataSourceHost() + "/"
                + getDatabaseName(testProfile)
                + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false";
    }

    private String getDataSourceHost() {
        final String envDatabaseHost = System.getenv("MWS_DATABASE_HOST");
        final String envDatabasePort = System.getenv("MWS_DATABASE_PORT");
        return isDatabaseHostDefined(envDatabaseHost, envDatabasePort) ? (envDatabaseHost + ":" + envDatabasePort) : "localhost";
    }

    private boolean isDatabaseHostDefined(final String envDatabaseHost, final String envDatabasePort) {
        return !StringUtils.isEmpty(envDatabaseHost) && !StringUtils.isEmpty(envDatabasePort);
    }

    private String getDatabaseName(final boolean testProfile) {
        if (testProfile) {
            return "mws_test_db";
        }

        final String envDatabaseName = System.getenv("MWS_DATABASE_NAME");
        return StringUtils.isEmpty(envDatabaseName) ? "mws_db" : envDatabaseName;
    }

    private String getDatabasePasswordForUser() {
        final String envDatabaseUserPassword = System.getenv("MWS_DATABASE_PASSWORD");
        return StringUtils.isEmpty(envDatabaseUserPassword) ? "password" : envDatabaseUserPassword;
    }

    private String getDatabaseUsername() {
        final String envDatabaseUserName = System.getenv("MWS_DATABASE_USERNAME");
        return StringUtils.isEmpty(envDatabaseUserName) ? "mws_dev" : envDatabaseUserName;
    }
}
