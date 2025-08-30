package co.com.crediya.application.consumer;

import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.auth.gateway.AuthGateway;
import co.com.crediya.application.model.exceptions.DuplicatedInfoException;
import co.com.crediya.application.model.exceptions.EntityNotFoundException;
import co.com.crediya.application.model.exceptions.GenericBadRequestException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthRestConsumer implements AuthGateway {
  private final WebClient client;

  @Override
  @CircuitBreaker(name = "findUserByIdNumber")
  public Mono<UserSummary> findUserByIdNumber(String idNumber) {
    return client
        .get()
        .uri("/" + idNumber)
        .retrieve()
        .onStatus(HttpStatusCode::isError, this::handleError)
        .bodyToMono(UserSummaryDTOResponse.class)
        .map(
            usrDTORes ->
                new UserSummary(
                    usrDTORes.id(),
                    usrDTORes.email(),
                    usrDTORes.firstName(),
                    usrDTORes.idType(),
                    usrDTORes.idNumber()))
        .doOnSubscribe(subscription -> log.info("Requesting user with idNumber: {}", idNumber))
        .doOnSuccess(dto -> log.info("Found usr: {}", dto))
        .doOnError(
            err -> log.error("Error while trying to get user for idNumber: {}", err.getMessage()));
  }

  private Mono<Throwable> handleError(ClientResponse res) {
    Map<HttpStatus, BiFunction<String, String, ? extends Throwable>> exceptionMap =
        getExceptionMap();

    HttpStatus status = HttpStatus.valueOf(res.statusCode().value());
    if (exceptionMap.containsKey(status)) {
      return this.handleKnownException(exceptionMap.get(status), res);
    }

    return Mono.error(
        new RuntimeException("Failed to communicate with auth gateway, status: " + status));
  }

  private Mono<Throwable> handleKnownException(
      BiFunction<String, String, ? extends Throwable> exceptionFactory, ClientResponse res) {

    return res.bodyToMono(BusinessExceptionResponse.class)
        .flatMap(
            errorBody ->
                Mono.error(exceptionFactory.apply(errorBody.attribute(), errorBody.details())));
  }

  private Map<HttpStatus, BiFunction<String, String, ? extends Throwable>> getExceptionMap() {
    return Map.of(
        HttpStatus.NOT_FOUND, EntityNotFoundException::new,
        HttpStatus.CONFLICT, DuplicatedInfoException::new,
        HttpStatus.BAD_REQUEST, GenericBadRequestException::new);
  }
}
