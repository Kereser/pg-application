package co.com.crediya.application.model.exceptions;

public class MissingValueOnRequiredFieldException extends BusinessException {
  private static final String BASE_MSG = "Missing value for required field";

  public MissingValueOnRequiredFieldException(String attribute, String reason) {
    super(BASE_MSG, attribute, reason);
  }
}
