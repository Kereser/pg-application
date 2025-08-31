package co.com.crediya.application.model.security;

import java.util.List;
import java.util.UUID;

import reactor.core.publisher.Mono;

public interface SecurityGateway {

  Mono<SecurityDetails> getDetailsFromContext();

  Mono<UUID> getCurrentUserId();

  Mono<List<String>> getRoles();
}
