package co.com.crediya.application.model.exceptions;

public class DuplicatedInfoException extends BusinessException {
  private static final String BASE_MSG = "Duplicated information found";

  public DuplicatedInfoException(String attribute, String reason) {
    super(BASE_MSG, attribute, reason);
  }
}
