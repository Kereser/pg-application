package co.com.crediya.application.model.producttype.vo;

import co.com.crediya.application.model.exceptions.Fields;
import co.com.crediya.application.model.exceptions.MissingValueOnRequiredFieldException;
import co.com.crediya.application.model.exceptions.PlainErrors;

public record ProductName(String value) {

  public ProductName {
    if (value == null || value.isBlank()) {
      throw new MissingValueOnRequiredFieldException(
          Fields.PRODUCT_NAME.getName(), PlainErrors.NOT_EMPY.getName());
    }
  }
}
