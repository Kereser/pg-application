package co.com.crediya.application.r2dbc.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.client.SSLMode;
import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class PostgresSQLConnectionPool {
  public static final int INITIAL_SIZE = 12;
  public static final int MAX_SIZE = 15;
  public static final int MAX_IDLE_TIME = 30;

  private final Environment env;

  @Bean
  public ConnectionPool getConnectionConfig(PostgresqlConnectionProperties properties) {
    SSLMode sslMode = env.acceptsProfiles(Profiles.of("prod")) ? SSLMode.REQUIRE : SSLMode.DISABLE;

    PostgresqlConnectionConfiguration dbConfiguration =
        PostgresqlConnectionConfiguration.builder()
            .host(properties.host())
            .port(properties.port())
            .database(properties.database())
            .schema(properties.schema())
            .username(properties.username())
            .password(properties.password())
            .sslMode(sslMode)
            .build();

    ConnectionPoolConfiguration poolConfiguration =
        ConnectionPoolConfiguration.builder()
            .connectionFactory(new PostgresqlConnectionFactory(dbConfiguration))
            .name("api-postgres-connection-pool")
            .initialSize(INITIAL_SIZE)
            .maxSize(MAX_SIZE)
            .maxIdleTime(Duration.ofMinutes(MAX_IDLE_TIME))
            .validationQuery("SELECT 1")
            .build();

    return new ConnectionPool(poolConfiguration);
  }
}
