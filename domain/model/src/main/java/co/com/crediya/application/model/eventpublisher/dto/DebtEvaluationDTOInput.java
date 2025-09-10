package co.com.crediya.application.model.eventpublisher.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebtEvaluationDTOInput {
  private String type;
  private Payload payload;

  @Builder(toBuilder = true)
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Payload {
    private UUID applicationId;
    private String newStatus;
  }
}
