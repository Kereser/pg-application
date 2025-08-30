package co.com.crediya.application.model.application.vo;

import java.math.BigDecimal;

import co.com.crediya.application.model.exceptions.*;

public record Amount(BigDecimal value) {
  private static final int MIN_VAL = 0;

  public Amount {
    if (value == null) {
      throw new MissingValueOnRequiredFieldException(
          Fields.AMOUNT.getName(), PlainErrors.NOT_EMPY.getName());
    }

    if (value.compareTo(BigDecimal.ZERO) == 0) {
      throw new IllegalValueForArgumentException(
          Fields.AMOUNT.getName(), TemplateErrors.MIN_VAL.buildMsg(MIN_VAL));
    }
  }
}
