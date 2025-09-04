package co.com.crediya.application.r2dbc.application;

import co.com.crediya.application.model.dto.GetApplicationFilteredCommand;
import co.com.crediya.application.r2dbc.entity.ApplicationEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationTemplateQueryRepository {
  Flux<ApplicationEntity> findAllFiltered(GetApplicationFilteredCommand filters);

  Mono<Long> countAllFiltered(GetApplicationFilteredCommand filters);
}
