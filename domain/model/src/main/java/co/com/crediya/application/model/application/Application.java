package co.com.crediya.application.model.application;

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
}
