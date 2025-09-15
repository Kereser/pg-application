package co.com.crediya.application.model.application;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder(toBuilder = true)
@Getter
@ToString
public class ApplicationSummary {
  private UUID userId;
  private UUID id;
  private BigDecimal amount;
  private int applicationPeriod;
}
