package co.com.crediya.application.model.exceptions;

public class EntityNotFoundException extends BusinessException {
  private static final String BASE_MSG = "Requested entity was not found";

  public EntityNotFoundException(String attribute, String reason) {
    super(BASE_MSG, attribute, reason);
  }
}
