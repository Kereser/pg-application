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
import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.auth.gateway.AuthGateway;
import co.com.crediya.application.model.exceptions.DuplicatedInfoException;
import co.com.crediya.application.model.exceptions.EntityNotFoundException;
import co.com.crediya.application.model.exceptions.GenericBadRequestException;
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

  private static final String IDS_PARAM = "ids";
  private static final String USERS_PATH = "/v1/users";
  private static final String SLASH = "/";

  @Override
  @CircuitBreaker(name = "findUserByIdNumber")
  public Mono<UserSummary> findUserByIdNumber(String idNumber) {
    return client
        .get()
        .uri(USERS_PATH + SLASH + idNumber)
        .retrieve()
        .onStatus(HttpStatusCode::isError, this::handleError)
        .bodyToMono(UserSummaryDTOResponse.class)
        .map(mapper::toDomain)
        .doOnSubscribe(subscription -> log.info("Requesting user with idNumber: {}", idNumber))
        .doOnSuccess(dto -> log.info("Found usr: {}", dto))
        .doOnError(
            err -> log.error("Error while trying to get user for idNumber: {}", err.getMessage()));
  }

  @Override
  @CircuitBreaker(name = "findUserByIdIn")
  public Flux<UserSummary> findUsersByIdIn(Set<UUID> userIds) {
    return client
        .get()
        .uri(builder -> builder.path(USERS_PATH).queryParam(IDS_PARAM, userIds.toArray()).build())
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
        .doOnSubscribe(subscription -> log.info("Requesting users with ids: {}", userIds))
        .doOnError(
            err ->
                log.error(
                    "Error while trying to get user for ids: {}. Error: {}",
                    userIds,
                    err.getMessage()));
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
                      String.format("Invalid conversion to known class. Status: %s", status))))
          .map(err -> this.handleKnownException(exceptionMap.get(status), err));
    }

    return Mono.error(
        new RuntimeException("Failed to communicate with auth gateway, status: " + status));
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
