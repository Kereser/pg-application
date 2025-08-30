package co.com.crediya.application.model.application.vo;

import co.com.crediya.application.model.exceptions.*;

public record IdNumber(String value) {
  private static final int MIN_VALUE = 5;
  private static final int MAX_VALUE = 20;

  public IdNumber {
    if (value == null || value.isBlank()) {
      throw new MissingValueOnRequiredFieldException(
          Fields.ID_NUMBER.getName(), PlainErrors.NOT_EMPY.getName());
    }

    if (value.trim().length() < MIN_VALUE || value.trim().length() > MAX_VALUE) {
      throw new IllegalValueForArgumentException(
          Fields.ID_NUMBER.getName(),
          TemplateErrors.LENGTH_BOUNDARIES.buildMsg(MIN_VALUE, MAX_VALUE));
    }
  }
}
