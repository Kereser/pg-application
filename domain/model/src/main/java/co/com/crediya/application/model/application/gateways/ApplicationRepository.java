package co.com.crediya.application.model.application.gateways;

import co.com.crediya.application.model.application.Application;
import reactor.core.publisher.Mono;

public interface ApplicationRepository {
  Mono<Application> save(Application application);
}
