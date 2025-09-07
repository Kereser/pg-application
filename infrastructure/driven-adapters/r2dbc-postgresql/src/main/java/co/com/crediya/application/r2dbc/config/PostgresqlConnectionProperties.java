package co.com.crediya.application.r2dbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import co.com.crediya.application.model.CommonConstants;

@ConfigurationProperties(prefix = CommonConstants.ConfigProperties.ADAPTERS_R2DBC)
public record PostgresqlConnectionProperties(
    String host, Integer port, String database, String schema, String username, String password) {}
