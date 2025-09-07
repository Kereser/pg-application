package co.com.crediya.application.model.application.gateways;

import java.util.UUID;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.application.dto.GetApplicationFilteredCommand;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationRepository {
  Mono<Application> save(Application application);

  Flux<Application> findAllFiltered(GetApplicationFilteredCommand filters);

  Mono<Long> countByFilers(GetApplicationFilteredCommand filters);

  Mono<Application> findById(UUID id);
}
