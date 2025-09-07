package co.com.crediya.application.model.application.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class GetApplicationFilteredCommand {
  private int page;
  private int size;
  private ApplicationFilters filters;

  @Getter
  @Builder(toBuilder = true)
  public static class ApplicationFilters {
    private UUID userId;
    private BigDecimal amount;
    private final Boolean haveManualReview;
    private final Set<UUID> productTypeIds;
  }
}
