package co.com.crediya.application.api.config.security.implementations;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import co.com.crediya.application.api.config.security.utils.JwtUtils;
import co.com.crediya.application.model.CommonConstants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {
  private final JwtUtils jwtUtils;

  @Override
  public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
    return Mono.empty();
  }

  @Override
  public Mono<SecurityContext> load(ServerWebExchange exchange) {
    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (authHeader != null && authHeader.startsWith(CommonConstants.Security.BEARER)) {
      String authToken = authHeader.substring(CommonConstants.Security.TOKEN_SUB_STR_LEN);
      if (jwtUtils.validateToken(authToken)) {
        UsernamePasswordAuthenticationToken authentication =
            jwtUtils.getAuthenticationInfo(authToken);
        authentication.setDetails(authToken);

        return Mono.just(new SecurityContextImpl(authentication));
      }
    }
    return Mono.empty();
  }
}
