package co.com.crediya.application.model.auth.gateway;

import java.util.Set;
import java.util.UUID;

import co.com.crediya.application.model.auth.UserSummary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthGateway {
  Mono<UserSummary> findUserByIdNumber(String idNumber);

  Flux<UserSummary> findUsersByIdIn(Set<UUID> userIds);
}
