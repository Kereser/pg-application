package co.com.crediya.application.model.exceptions;

public class ResourceOwnershipException extends BusinessException {
  private static final String BASE_MSG = "RESOURCE_OWNERSHIP_EXCEPTION";

  public ResourceOwnershipException(String attribute, String reason) {
    super(BASE_MSG, attribute, reason);
  }
}
