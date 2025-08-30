package co.com.crediya.application.model.application.vo;

import co.com.crediya.application.model.exceptions.*;

public record ApplicationPeriod(Integer value) {
  private static final int MIN_PERIOD = 1;
  private static final int MAX_PERIOD = 72;

  public ApplicationPeriod {
    if (value == null) {
      throw new MissingValueOnRequiredFieldException(
          Fields.APPLICATION_PERIOD.getName(), PlainErrors.NOT_EMPY.getName());
    }

    if (value < MIN_PERIOD || value > MAX_PERIOD) {
      throw new IllegalValueForArgumentException(
          Fields.APPLICATION_PERIOD.getName(),
          TemplateErrors.LENGTH_BOUNDARIES.buildMsg(MIN_PERIOD, MAX_PERIOD));
    }
  }
}
