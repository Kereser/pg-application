package co.com.crediya.application.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import co.com.crediya.application.model.CommonConstants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = CommonConstants.ConfigProperties.ROUTES)
public class Routes {
  private Paths paths;

  @Getter
  @Setter
  public static class Paths {
    private String base;
    private String application;
    private String applicationsToReview;
  }
}
