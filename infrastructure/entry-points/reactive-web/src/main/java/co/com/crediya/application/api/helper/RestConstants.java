package co.com.crediya.application.api.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestConstants {

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Methods {
    public static final String POST = "POST";
    public static final String GET = "GET";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class StatusCodeInt {
    public static final String OK = "200";
    public static final String BAD_REQUEST = "400";
    public static final String CONFLICT = "409";
    public static final String FORBIDDEN = "403";
    public static final String SERVER_ERROR = "500";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class ApplicationAPI {
    public static final String ROOT = "/api/v1";
    public static final String BASE = "/api/v1/applications";
    public static final String REVIEW = "api/v1/applications/manual_review";
  }
}
