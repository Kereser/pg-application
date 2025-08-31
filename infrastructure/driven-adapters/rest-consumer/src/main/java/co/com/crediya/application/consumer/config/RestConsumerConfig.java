package co.com.crediya.application.consumer.config;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class RestConsumerConfig {

  private final String url;
  private final int timeout;
  private static final String BEARER = "Bearer ";

  public RestConsumerConfig(
      @Value("${adapter.restconsumer.url}") String url,
      @Value("${adapter.restconsumer.timeout}") int timeout) {
    this.url = url;
    this.timeout = timeout;
  }

  @Bean
  public WebClient getWebClient(WebClient.Builder builder) {
    return builder
        .baseUrl(url)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .clientConnector(getClientHttpConnector())
        .filter(authHandlerFiler())
        .build();
  }

  @Bean
  public ExchangeFilterFunction authHandlerFiler() {
    return (clientRequest, next) ->
        ReactiveSecurityContextHolder.getContext()
            .flatMap(
                securityContext -> {
                  Authentication authentication = securityContext.getAuthentication();

                  if (authentication instanceof UsernamePasswordAuthenticationToken
                      && authentication.getDetails() instanceof String token) {
                    ClientRequest authorizedRequest =
                        ClientRequest.from(clientRequest)
                            .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                            .build();

                    return next.exchange(authorizedRequest);
                  }

                  return next.exchange(clientRequest);
                })
            .switchIfEmpty(next.exchange(clientRequest));
  }

  private ClientHttpConnector getClientHttpConnector() {
    /*
    IF YO REQUIRE APPEND SSL CERTIFICATE SELF SIGNED: this should be in the default cacerts trustore
    */
    return new ReactorClientHttpConnector(
        HttpClient.create()
            .compress(true)
            .keepAlive(true)
            .option(CONNECT_TIMEOUT_MILLIS, timeout)
            .doOnConnected(
                connection -> {
                  connection.addHandlerLast(new ReadTimeoutHandler(timeout, MILLISECONDS));
                  connection.addHandlerLast(new WriteTimeoutHandler(timeout, MILLISECONDS));
                }));
  }
}
