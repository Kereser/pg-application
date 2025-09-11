package co.com.crediya.application.model.application;

import java.math.BigDecimal;
import java.util.UUID;

import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.producttype.ProductType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder(toBuilder = true)
@Getter
@ToString
public class ApplicationUserSummary {
  private UUID userId;
  private UUID id;
  private String email;
  private String name;
  private BigDecimal baseSalary;
  private ProductType productType;
  private ApplicationStatus status;
  private BigDecimal amount;
  private int applicationPeriod;
  private BigDecimal monthPayment;
}
