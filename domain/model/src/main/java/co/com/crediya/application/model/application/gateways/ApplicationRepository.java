package co.com.crediya.application.model.application.gateways;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.dto.GetApplicationFilteredCommand;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationRepository {
  Mono<Application> save(Application application);

  Flux<Application> findAllFiltered(GetApplicationFilteredCommand filters);

  Mono<Long> countByFilers(GetApplicationFilteredCommand filters);
}
