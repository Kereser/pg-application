package co.com.crediya.application.model.applicationstatus;

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
public class ApplicationStatus {
  private UUID id;
  private ApplicationStatusName name;
  private String description;
}
