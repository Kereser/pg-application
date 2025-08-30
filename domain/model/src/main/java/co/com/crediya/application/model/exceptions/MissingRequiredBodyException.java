package co.com.crediya.application.model.exceptions;

public class MissingRequiredBodyException extends BusinessException {
  private static final String BASE_MSG = "Required body is missing";

  public MissingRequiredBodyException(String attribute, String reason) {
    super(BASE_MSG, attribute, reason);
  }
}
