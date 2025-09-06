package co.com.crediya.application.model.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlainErrors {
  NOT_EMPY("Must not be empty or blank"),
  MISSING_REQUIRED_BODY("Missing required body"),
  AUTH_GATEWAY_ERROR("Failed to communicate with auth gateway, status: "),
  OWNERSHIP("Resource operation invalid due to ownership");

  private final String name;
}
