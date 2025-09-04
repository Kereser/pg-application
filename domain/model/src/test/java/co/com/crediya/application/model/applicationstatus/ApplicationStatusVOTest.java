package co.com.crediya.application.model.applicationstatus;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.crediya.application.model.exceptions.Entities;
import co.com.crediya.application.model.exceptions.IllegalValueForArgumentException;
import co.com.crediya.application.model.exceptions.MissingValueOnRequiredFieldException;

@ExtendWith(MockitoExtension.class)
public class ApplicationStatusVOTest {

  @Nested
  class ApplicationStatusNameTest {
    @Test
    void shouldThrowExceptionWhenValueIsNull() {
      assertThrows(
          MissingValueOnRequiredFieldException.class, () -> ApplicationStatusName.fromName(null));
    }

    @Test
    void shouldThrowExceptionWhenNotFoundInEnum() {
      assertThrows(
          IllegalValueForArgumentException.class,
          () -> ApplicationStatusName.fromName(Entities.APPLICATION.name()));
    }

    @Test
    void shouldReturnNameIfValidValueIsProvided() {
      ApplicationStatusName appStatusName =
          assertDoesNotThrow(
              () -> ApplicationStatusName.fromName(ApplicationStatusName.PENDING.getName()));
      assertEquals(appStatusName.getName(), ApplicationStatusName.PENDING.getName());
    }
  }
}
