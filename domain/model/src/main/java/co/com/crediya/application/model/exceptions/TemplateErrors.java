package co.com.crediya.application.model.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TemplateErrors {
  X_NOT_FOUND_FOR_Y("%s was not found for %s"),
  X_ALREADY_ASSIGNED_FOR_Y("%s is already assigned for %s"),
  X_NOT_VALID_VALUE_FOR_Y_WITH_RANGE("%s not valid value for %s. Valid ranges: %s to %s"),
  X_NOT_VALID_FORMAT_FOR_Y("Value %s does not meet format condition for %s field"),
  LENGTH_BOUNDARIES("Value must be between %s and %s"),
  MIN_VAL("Min val must be at least: %s"),

  INVALID_CONVERSION_TO_CLASS("Invalid conversion to known class. Status: %s");

  private final String msg;

  public String buildMsg(Object... args) {
    return String.format(msg, args);
  }
}
