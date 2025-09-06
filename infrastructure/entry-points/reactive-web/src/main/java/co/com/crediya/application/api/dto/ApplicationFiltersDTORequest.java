package co.com.crediya.application.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.web.reactive.function.server.ServerRequest;

import co.com.crediya.application.model.CommonConstants;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationFiltersDTORequest {
  private final int page;
  private final int size;
  private final ApplicationFilters filters;

  public static ApplicationFiltersDTORequest from(ServerRequest request) {
    int page =
        request
            .queryParam(CommonConstants.QueryParams.PAGE)
            .map(Integer::parseInt)
            .orElse(CommonConstants.QueryParams.PAGE_DEF);
    int size =
        request
            .queryParam(CommonConstants.QueryParams.SIZE)
            .map(Integer::parseInt)
            .orElse(CommonConstants.QueryParams.SIZE_DEF);

    ApplicationFilters filters = ApplicationFilters.from(request);

    return ApplicationFiltersDTORequest.builder().page(page).size(size).filters(filters).build();
  }

  @Getter
  @Builder(toBuilder = true)
  public static class ApplicationFilters {
    private final UUID userId;
    private final BigDecimal amount;
    private final Boolean haveManualReview;

    public static ApplicationFilters from(ServerRequest request) {
      ApplicationFiltersBuilder filtersBuilder = ApplicationFilters.builder();

      request
          .queryParam(CommonConstants.Fields.USER_ID)
          .map(UUID::fromString)
          .ifPresent(filtersBuilder::userId);
      request
          .queryParam(CommonConstants.Fields.AMOUNT)
          .map(BigDecimal::new)
          .ifPresent(filtersBuilder::amount);
      request
          .queryParam(CommonConstants.QueryParams.MANUAL_REVIEW_QUERY)
          .map(Boolean::valueOf)
          .ifPresent(filtersBuilder::haveManualReview);

      return filtersBuilder.build();
    }
  }
}
