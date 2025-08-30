package co.com.crediya.application.model.application;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.crediya.application.model.application.vo.Amount;
import co.com.crediya.application.model.application.vo.ApplicationPeriod;
import co.com.crediya.application.model.application.vo.IdNumber;
import co.com.crediya.application.model.exceptions.IllegalValueForArgumentException;
import co.com.crediya.application.model.exceptions.MissingValueOnRequiredFieldException;

@ExtendWith(MockitoExtension.class)
class ApplicationVOTest {
  @Nested
  class AmountTests {

    @Test
    void shouldCreateSuccessfullyWithValidValue() {
      BigDecimal validAmount = new BigDecimal("1500.50");
      Amount amount = assertDoesNotThrow(() -> new Amount(validAmount));
      assertEquals(0, validAmount.compareTo(amount.value()));
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
      assertThrows(MissingValueOnRequiredFieldException.class, () -> new Amount(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsZero() {
      assertThrows(IllegalValueForArgumentException.class, () -> new Amount(BigDecimal.ZERO));
    }
  }

  @Nested
  class ApplicationPeriodTests {

    @Test
    void shouldCreateSuccessfullyWithValidValue() {
      Integer validPeriod = 36;
      ApplicationPeriod period = assertDoesNotThrow(() -> new ApplicationPeriod(validPeriod));
      assertEquals(validPeriod, period.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
      assertThrows(MissingValueOnRequiredFieldException.class, () -> new ApplicationPeriod(null));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 73})
    void shouldThrowExceptionWhenValueIsOutOfRange(int invalidPeriod) {
      assertThrows(
          IllegalValueForArgumentException.class, () -> new ApplicationPeriod(invalidPeriod));
    }
  }

  @Nested
  class IdNumberTests {
    @Test
    void shouldCreateSuccessfullyWithValidValue() {
      String validId = "123456789";
      IdNumber idNumber = assertDoesNotThrow(() -> new IdNumber(validId));
      assertEquals(validId, idNumber.value());
    }

    @Test
    @DisplayName("Debería lanzar MissingValueOnRequiredFieldException si el valor es nulo")
    void shouldThrowExceptionWhenValueIsNull() {
      assertThrows(MissingValueOnRequiredFieldException.class, () -> new IdNumber(null));
    }

    @Test
    @DisplayName("Debería lanzar MissingValueOnRequiredFieldException si el valor está en blanco")
    void shouldThrowExceptionWhenValueIsBlank() {
      assertThrows(MissingValueOnRequiredFieldException.class, () -> new IdNumber("   "));
    }

    @Test
    @DisplayName("Debería lanzar IllegalValueForArgumentException si el valor es demasiado corto")
    void shouldThrowExceptionWhenValueIsTooShort() {
      assertThrows(IllegalValueForArgumentException.class, () -> new IdNumber("1234"));
    }

    @Test
    @DisplayName("Debería lanzar IllegalValueForArgumentException si el valor es demasiado largo")
    void shouldThrowExceptionWhenValueIsTooLong() {
      String longId = "1".repeat(21);
      assertThrows(IllegalValueForArgumentException.class, () -> new IdNumber(longId));
    }
  }
}
