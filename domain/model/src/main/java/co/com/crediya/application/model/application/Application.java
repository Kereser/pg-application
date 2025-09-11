package co.com.crediya.application.model.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import co.com.crediya.application.model.application.vo.*;
import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.applicationstatus.ApplicationStatusName;
import co.com.crediya.application.model.producttype.ProductType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Application {
  private UUID id;
  private Amount amount;
  private UUID userId;
  private ApplicationPeriod applicationPeriod;
  private ApplicationStatus applicationStatus;
  private ProductType productType;

  public boolean isPendingApplication() {
    return this.applicationStatus.getName().equals(ApplicationStatusName.PENDING);
  }

  public BigDecimal getMonthPayment() {
    if (this.productType.getInterestRate() == null) {
      return null;
    }

    BigDecimal principal = this.amount.value();
    BigDecimal annualRate = this.productType.getInterestRate();
    int months = this.applicationPeriod.value();

    BigDecimal onePlusAnnual = BigDecimal.ONE.add(annualRate);
    BigDecimal monthlyRate =
        BigDecimal.valueOf(Math.pow(onePlusAnnual.doubleValue(), 1.0 / 12.0) - 1);

    BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
    BigDecimal factor = onePlusRate.pow(months);

    BigDecimal numerator = principal.multiply(monthlyRate).multiply(factor);
    BigDecimal denominator = factor.subtract(BigDecimal.ONE);

    return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
  }
}
