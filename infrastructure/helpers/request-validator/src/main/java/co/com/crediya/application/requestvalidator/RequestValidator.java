package co.com.crediya.application.requestvalidator;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import co.com.crediya.application.model.exceptions.MissingRequiredBodyException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public record RequestValidator(Validator validator) {
  public <T> Mono<T> validate(ServerRequest request, Class<T> bodyClass, String targetClass) {
    return request
        .bodyToMono(bodyClass)
        .switchIfEmpty(Mono.error(new RuntimeException("Missing required body")))
        .flatMap(
            body -> {
              Set<ConstraintViolation<T>> violations = validator.validate(body);

              if (violations.isEmpty()) {
                return Mono.just(body);
              }

              String errorMessages =
                  violations.stream()
                      .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                      .collect(Collectors.joining(", "));

              return Mono.error(new MissingRequiredBodyException(targetClass, errorMessages));
            });
  }
}
