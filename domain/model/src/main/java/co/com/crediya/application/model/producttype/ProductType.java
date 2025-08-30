package co.com.crediya.application.model.producttype;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductType {
  private UUID id;
  private String name;
  private BigDecimal minAmount;
  private BigDecimal maxAmount;
  private BigDecimal interestRate;
  private boolean autoValidation;
}
