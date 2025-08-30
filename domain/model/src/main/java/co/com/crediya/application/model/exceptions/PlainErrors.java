package co.com.crediya.application.model.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlainErrors {
  NOT_EMPY("Must not be empty or blank");

  private final String name;
}
