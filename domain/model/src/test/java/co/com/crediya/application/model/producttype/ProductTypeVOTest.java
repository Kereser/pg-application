package co.com.crediya.application.model.producttype;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.crediya.application.model.exceptions.MissingValueOnRequiredFieldException;
import co.com.crediya.application.model.producttype.vo.ProductName;

@ExtendWith(MockitoExtension.class)
class ProductTypeVOTest {

  @Nested
  class ProductNameTest {
    @Test
    void shouldThrowExceptionWhenValueIsNull() {
      assertThrows(MissingValueOnRequiredFieldException.class, () -> new ProductName(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
      assertThrows(MissingValueOnRequiredFieldException.class, () -> new ProductName(""));
    }
  }
}
