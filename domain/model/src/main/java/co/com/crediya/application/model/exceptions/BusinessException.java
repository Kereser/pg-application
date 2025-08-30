package co.com.crediya.application.model.exceptions;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {
  private final String reason;
  private final String attribute;

  protected BusinessException(String msg, String attribute, String reason) {
    super(msg);
    this.attribute = attribute;
    this.reason = reason;
  }
}
