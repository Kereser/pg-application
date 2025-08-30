package co.com.crediya.application.model.auth.gateway;

import co.com.crediya.application.model.auth.UserSummary;
import reactor.core.publisher.Mono;

public interface AuthGateway {
  Mono<UserSummary> findUserByIdNumber(String idNumber);
}
