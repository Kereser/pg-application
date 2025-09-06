package co.com.crediya.application.api.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import co.com.crediya.application.api.helper.RestConstants;
import co.com.crediya.application.model.CommonConstants;

@Configuration
public class CorsConfig {

  @Bean
  CorsWebFilter corsWebFilter(@Value("${cors.allowed-origins}") String origins) {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOrigins(List.of(origins.split(CommonConstants.Chars.COMMA_DELIMITER)));
    config.setAllowedMethods(Arrays.asList(RestConstants.Methods.POST, RestConstants.Methods.GET));
    config.setAllowedHeaders(List.of(CorsConfiguration.ALL));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration(CommonConstants.Chars.PATH_ALL, config);

    return new CorsWebFilter(source);
  }
}
