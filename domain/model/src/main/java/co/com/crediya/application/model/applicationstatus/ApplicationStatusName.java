package co.com.crediya.application.model.applicationstatus;

import java.util.HashMap;
import java.util.Map;

import co.com.crediya.application.model.exceptions.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ApplicationStatusName {
  PENDING("PENDING"),
  REJECTED("REJECTED"),
  APPROVED("APPROVED"),
  MANUAL_REVISION("MANUAL_REVISION");

  private final String name;

  private static final Map<String, ApplicationStatusName> BY_NAME = new HashMap<>();

  static {
    for (ApplicationStatusName n : values()) {
      BY_NAME.put(n.name, n);
    }
  }

  public static ApplicationStatusName fromName(String name) {
    if (name == null) {
      throw new MissingValueOnRequiredFieldException(
          Fields.APPLICATION_STATUS_NAME.getName(), PlainErrors.NOT_EMPY.getName());
    }

    String nameUpp = name.toUpperCase();
    ApplicationStatusName statusName = BY_NAME.get(nameUpp);

    if (statusName == null) {
      throw new IllegalValueForArgumentException(
          Fields.APPLICATION_STATUS_NAME.getName(),
          TemplateErrors.X_NOT_VALID_FORMAT_FOR_Y.buildMsg(
              nameUpp, Fields.APPLICATION_STATUS_NAME.getName()));
    }

    return statusName;
  }
}
