package co.com.crediya.application.api.config.security.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import co.com.crediya.application.api.config.security.utils.CustomUserDetails;
import co.com.crediya.application.model.security.SecurityDetails;
import co.com.crediya.application.model.security.SecurityGateway;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SecurityContextAdapter implements SecurityGateway {
  public Mono<Authentication> getAuthentication() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .filter(Authentication::isAuthenticated);
  }

  @Override
  public Mono<SecurityDetails> getDetailsFromContext() {
    return Mono.zip(getCurrentUserId(), getRoles())
        .map(tuple -> new SecurityDetails(tuple.getT1(), tuple.getT2()))
        .doOnSubscribe(sub -> log.info("Getting details from context."))
        .doOnSuccess(res -> log.info("Returned details from context: {}", res))
        .doOnError(
            err -> log.error("Failed to get details from context. Details: {}", err.getMessage()));
  }

  @Override
  public Mono<UUID> getCurrentUserId() {
    return this.getAuthentication()
        .map(auth -> ((CustomUserDetails) auth.getPrincipal()).getUserId());
  }

  @Override
  public Mono<List<String>> getRoles() {
    return this.getAuthentication()
        .map(auth -> auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
  }
}
