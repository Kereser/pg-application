package co.com.crediya.application.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "routes.paths")
public class ApplicationPath {
  private String base;
  private String application;
}
