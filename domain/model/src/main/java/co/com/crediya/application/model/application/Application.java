package co.com.crediya.application.model.application;

import java.util.UUID;

import co.com.crediya.application.model.application.vo.*;
import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.producttype.ProductType;
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
public class Application {
  private UUID id;
  private Amount amount;
  private UUID userId;
  private ApplicationPeriod applicationPeriod;
  private ApplicationStatus applicationStatus;
  private ProductType productType;
}
