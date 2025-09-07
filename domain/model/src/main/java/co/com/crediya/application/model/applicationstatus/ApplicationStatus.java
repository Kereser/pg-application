package co.com.crediya.application.model.applicationstatus;

import java.util.UUID;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class ApplicationStatus {
  private UUID id;
  private ApplicationStatusName name;
  private String description;
}
