package co.com.crediya.application.consumer;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import co.com.crediya.application.consumer.helper.AuthRestMapper;
import co.com.crediya.application.model.CommonConstants;
import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.auth.gateway.AuthGateway;
import co.com.crediya.application.model.exceptions.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthRestConsumer implements AuthGateway {
  private final WebClient client;
  private final AuthRestMapper mapper;

  private static final String LOG_FIND_USER_BY_ID_SUBSCRIBE = "Requesting user with idNumber: {}";
  private static final String LOG_FIND_USER_BY_ID_SUCCESS = "Found usr: {}";
  private static final String LOG_FIND_USER_BY_ID_ERROR =
      "Error while trying to get user for idNumber: {}";
  private static final String LOG_FIND_USERS_BY_IDS_SUBSCRIBE = "Requesting users with ids: {}";
  private static final String LOG_FIND_USERS_BY_IDS_ERROR =
      "Error while trying to get user for ids: {}. Error: {}";

  @Override
  @CircuitBreaker(name = CommonConstants.AuthConsumerCircuitBreaker.FIND_USER_BY_ID_NUMBER)
  public Mono<UserSummary> findUserByIdNumber(String idNumber) {
    return client
        .get()
        .uri(CommonConstants.Endpoints.USERS_COMPLETE + idNumber)
        .retrieve()
        .onStatus(HttpStatusCode::isError, this::handleError)
        .bodyToMono(UserSummaryDTOResponse.class)
        .map(mapper::toDomain)
        .doOnSubscribe(subscription -> log.info(LOG_FIND_USER_BY_ID_SUBSCRIBE, idNumber))
        .doOnSuccess(dto -> log.info(LOG_FIND_USER_BY_ID_SUCCESS, dto))
        .doOnError(err -> log.error(LOG_FIND_USER_BY_ID_ERROR, err.getMessage()));
  }

  @Override
  @CircuitBreaker(name = CommonConstants.AuthConsumerCircuitBreaker.FIND_USER_BY_ID_IN)
  public Flux<UserSummary> findUsersByIdIn(Set<UUID> userIds) {
    return client
        .get()
        .uri(
            builder ->
                builder
                    .path(CommonConstants.Endpoints.USERS)
                    .queryParam(CommonConstants.QueryParams.IDS, userIds.toArray())
                    .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, this::handleError)
        .bodyToFlux(UserSummaryDTOResponse.class)
        .map(
            usrDTORes ->
                new UserSummary(
                    usrDTORes.id(),
                    usrDTORes.email(),
                    usrDTORes.baseSalary(),
                    usrDTORes.firstName(),
                    usrDTORes.idType(),
                    usrDTORes.idNumber()))
        .doOnSubscribe(subscription -> log.info(LOG_FIND_USERS_BY_IDS_SUBSCRIBE, userIds))
        .doOnError(err -> log.error(LOG_FIND_USERS_BY_IDS_ERROR, userIds, err.getMessage()));
  }

  private Mono<Throwable> handleError(ClientResponse res) {
    Map<HttpStatus, BiFunction<String, String, ? extends Throwable>> exceptionMap =
        getExceptionMap();

    HttpStatus status = HttpStatus.valueOf(res.statusCode().value());
    if (exceptionMap.containsKey(status)) {
      return res.bodyToMono(BusinessExceptionResponse.class)
          .switchIfEmpty(
              Mono.error(
                  new RuntimeException(
                      TemplateErrors.INVALID_CONVERSION_TO_CLASS.buildMsg(status))))
          .map(err -> this.handleKnownException(exceptionMap.get(status), err));
    }

    return Mono.error(new RuntimeException(PlainErrors.AUTH_GATEWAY_ERROR.getName() + status));
  }

  private Throwable handleKnownException(
      BiFunction<String, String, ? extends Throwable> exceptionFactory,
      BusinessExceptionResponse errRes) {

    return exceptionFactory.apply(errRes.attribute(), errRes.details());
  }

  private Map<HttpStatus, BiFunction<String, String, ? extends Throwable>> getExceptionMap() {
    return Map.of(
        HttpStatus.NOT_FOUND, EntityNotFoundException::new,
        HttpStatus.CONFLICT, DuplicatedInfoException::new,
        HttpStatus.BAD_REQUEST, GenericBadRequestException::new);
  }
}
