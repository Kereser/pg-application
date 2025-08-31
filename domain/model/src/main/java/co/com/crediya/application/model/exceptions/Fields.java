package co.com.crediya.application.model.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Fields {
  AMOUNT("Amount"),
  PRODUCT_NAME("Product name"),
  ID_NUMBER("Id number"),
  USER_ID("User id"),
  APPLICATION_PERIOD("Application period");

  private final String name;
}
