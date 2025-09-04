package co.com.crediya.application.r2dbc.application;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.application.gateways.ApplicationRepository;
import co.com.crediya.application.model.dto.GetApplicationFilteredCommand;
import co.com.crediya.application.r2dbc.application.mapper.ApplicationMapperStandard;
import co.com.crediya.application.r2dbc.entity.ApplicationEntity;
import co.com.crediya.application.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class ApplicationRepositoryAdapter
    extends ReactiveAdapterOperations<
        Application, ApplicationEntity, UUID, ApplicationReactiveRepository>
    implements ApplicationRepository {
  private final TransactionalOperator txOperator;

  public ApplicationRepositoryAdapter(
      ApplicationReactiveRepository repository,
      ApplicationMapperStandard mapper,
      TransactionalOperator txOperator) {
    super(repository, mapper::toEntity, mapper::toData);
    this.txOperator = txOperator;
  }

  @Override
  public Mono<Long> countByFilers(GetApplicationFilteredCommand filters) {
    return repository.countAllFiltered(filters);
  }

  @Override
  public Flux<Application> findAllFiltered(GetApplicationFilteredCommand filters) {
    return repository.findAllFiltered(filters).map(super::toEntity).as(txOperator::transactional);
  }

  @Override
  public Mono<Application> save(Application entity) {
    return super.save(entity)
        .as(txOperator::transactional)
        .doOnSubscribe(sub -> log.info("Saving Application {}", entity))
        .doOnSuccess(res -> log.info("Saved application: {}", res))
        .doOnError(
            err ->
                log.error("Could not save application: {}. Details: {}", entity, err.getMessage()));
  }
}
