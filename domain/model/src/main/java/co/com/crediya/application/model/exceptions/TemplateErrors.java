package co.com.crediya.application.model.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TemplateErrors {
  X_NOT_FOUND_FOR_Y("%s was not found for %s"),
  X_NOT_VALID_VALUE_FOR_Y_WITH_RANGE("%s not valid value for %s. Valid ranges: %s to %s"),
  LENGTH_BOUNDARIES("Value must be between %s and %s"),
  MIN_VAL("Min val must be at least: %s");

  private final String msg;

  public String buildMsg(Object... args) {
    return String.format(msg, args);
  }
}
