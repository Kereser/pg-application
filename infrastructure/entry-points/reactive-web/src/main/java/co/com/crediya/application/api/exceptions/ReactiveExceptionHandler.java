package co.com.crediya.application.api.exceptions;

import java.util.Map;

import co.com.crediya.application.model.CommonConstants;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;

import co.com.crediya.application.model.exceptions.BusinessException;
import reactor.core.publisher.Mono;

@Component
public class ReactiveExceptionHandler extends AbstractErrorWebExceptionHandler {
  private final Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode;
  private final HttpStatus defaultStatus;

  public ReactiveExceptionHandler(
      ErrorAttributes errorAttributes,
      WebProperties.Resources resources,
      ApplicationContext applicationContext,
      Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode,
      ServerCodecConfigurer configurer,
      HttpStatus defaultStatus) {
    super(errorAttributes, resources, applicationContext);
    this.setMessageWriters(configurer.getWriters());
    this.setMessageReaders(configurer.getReaders());
    this.exceptionToStatusCode = exceptionToStatusCode;
    this.defaultStatus = defaultStatus;
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
  }

  private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
    Throwable error = getError(request);
    HttpStatus httpStatus;
    if (error instanceof BusinessException exception) {
      httpStatus = exceptionToStatusCode.getOrDefault(exception.getClass(), defaultStatus);

      ErrorResponseDTO dto =
          new ErrorResponseDTO(
              exception.getAttribute(),
              httpStatus.value(),
              exception.getMessage(),
              exception.getReason() == null ? CommonConstants.Chars.EMPTY : exception.getReason());

      return ServerResponse.status(httpStatus)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(dto);
    } else {
      httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      return ServerResponse.status(httpStatus)
          .contentType(MediaType.APPLICATION_JSON)
          .body(
              BodyInserters.fromValue(
                  ErrorResponse.create(
                      error, HttpStatusCode.valueOf(httpStatus.value()), error.getMessage())));
    }
  }
}
