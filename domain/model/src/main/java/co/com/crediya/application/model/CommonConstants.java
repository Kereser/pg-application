package co.com.crediya.application.model;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstants {

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Chars {
    public static final String COMMA_DELIMITER_SPACE = ", ";
    public static final String COMMA_DELIMITER = ",";
    public static final String COLON_DELIMITER_SPACE = ": ";
    public static final String EMPTY = "";
    public static final String PATH_ALL = "/**";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class ErrorResponse {
    // Examples
    public static final String ATTRIBUTE_DESCRIPTION = "Attribute or field";
    public static final String STATUS_DESCRIPTION = "Http status code";
    public static final String ERROR_DESCRIPTION = "Generic error for class";
    public static final String DETAILS_DESCRIPTION = "Detailed error";

    // Generic messages
    public static final String ERROR_EXAMPLE = "Field validation exception";
    public static final String DETAILS_EXAMPLE =
        "exampleValue is not a valid value for exampleAttribute";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Fields {
    public static final String EMAIL = "email";
    public static final String USER_ID = "userId";
    public static final String PASSWORD = "password";
    public static final String AUTHORITIES = "authorities";
    public static final String AMOUNT = "amount";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Mappers {
    public static final String FILTERS_PRODUCT_TYPE_IDS = "filters.productTypeIds";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Amount {
    public static final BigDecimal ONE_M = BigDecimal.valueOf(1_000_000);
    public static final BigDecimal TEN_M = BigDecimal.valueOf(10_000_000);
    public static final BigDecimal FIVE_M = BigDecimal.valueOf(5_000_000);
    public static final BigDecimal TWO_H_M = BigDecimal.valueOf(200_000_000);
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class ProductTypeName {
    public static final String FREE_INVESTMENT_LOAN = "FREE_INVESTMENT_LOAN";
    public static final String VEHICLE_LOAN = "VEHICLE_LOAN";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class ConfigProperties {
    public static final String ADAPTERS_R2DBC = "adapters.r2dbc";
    public static final String ADAPTER_SQS = "adapter.sqs";
    public static final String ROUTES = "routes";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Security {
    public static final String BEARER = "Bearer ";
    public static final String MANAGER_ROLE = "ROLE_MANAGER";
    public static final int TOKEN_SUB_STR_LEN = 7;
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class QueryParams {
    public static final String MANUAL_REVIEW_QUERY = "manual_review";
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String IDS = "ids";
    public static final int PAGE_DEF = 0;
    public static final int SIZE_DEF = 2;
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class PathVariables {
    public static final String APPLICATION_ID = "applicationId";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Endpoints {
    // Auth
    public static final String USERS = "/api/v1/users";
    public static final String USERS_COMPLETE = "/api/v1/users/";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class AuthConsumerCircuitBreaker {
    public static final String FIND_USER_BY_ID_NUMBER = "findUserByIdNumber";
    public static final String FIND_USER_BY_ID_IN = "findUserByIdIn";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Docs {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Application {
      public static final String SAVE_SUMMARY = "Create a new application";
      public static final String SAVE_DESCRIPTION = "Application creation request";
      public static final String SAVE_OPERATION_ID = "createApplication";

      public static final String GET_FOR_MANUAL_REVIEW_SUMMARY = "Get applications";
      public static final String GET_FOR_MANUAL_REVIEW_DESCRIPTION =
          "List of applications marked as manual review";
      public static final String GET_FOR_MANUAL_REVIEW_OPERATION_ID =
          "getApplicationsForManualReview";

      public static final String PATCH_UPDATE_STATUS_SUMMARY = "Update application status";
      public static final String PATCH_UPDATE_STATUS_DESCRIPTION =
          "Update a given applicationId with new status";
      public static final String PATCH_UPDATE_STATUS_OPERATION_ID = "updateApplicationStatus";
    }
  }
}
