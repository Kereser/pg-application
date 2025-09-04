package co.com.crediya.application.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.web.reactive.function.server.ServerRequest;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationFiltersDTORequest {
  private final int page;
  private final int size;
  private final ApplicationFilters filters;

  private static final int PAGE_DEF = 0;
  private static final int SIZE_DEF = 2;

  private static final String PAGE_STR = "page";
  private static final String SIZE_STR = "size";

  public static ApplicationFiltersDTORequest from(ServerRequest request) {
    int page = request.queryParam(PAGE_STR).map(Integer::parseInt).orElse(PAGE_DEF);
    int size = request.queryParam(SIZE_STR).map(Integer::parseInt).orElse(SIZE_DEF);

    ApplicationFilters filters = ApplicationFilters.from(request);

    return ApplicationFiltersDTORequest.builder().page(page).size(size).filters(filters).build();
  }

  @Getter
  @Builder(toBuilder = true)
  public static class ApplicationFilters {
    private final UUID userId;
    private final BigDecimal amount;
    private final Boolean haveManualReview;

    private static final String USER_ID = "userId";
    private static final String AMOUNT_STR = "amount";
    private static final String MANUAL_REVIEW = "manual_review";

    public static ApplicationFilters from(ServerRequest request) {
      ApplicationFiltersBuilder filtersBuilder = ApplicationFilters.builder();

      request.queryParam(USER_ID).map(UUID::fromString).ifPresent(filtersBuilder::userId);
      request.queryParam(AMOUNT_STR).map(BigDecimal::new).ifPresent(filtersBuilder::amount);
      request
          .queryParam(MANUAL_REVIEW)
          .map(Boolean::valueOf)
          .ifPresent(filtersBuilder::haveManualReview);

      return filtersBuilder.build();
    }
  }
}
