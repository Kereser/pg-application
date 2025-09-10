package co.com.crediya.application.r2dbc.application;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.application.dto.GetApplicationFilteredCommand;
import co.com.crediya.application.model.application.gateways.ApplicationRepository;
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

  private static final String LOG_SAVE_SUBSCRIBE = "Saving application {}";
  private static final String LOG_SAVE_SUCCESS = "Saved application: {}";
  private static final String LOG_SAVE_ERROR = "Could not save application: {}. Error: {}";

  private static final String LOG_FIND_BY_ID_SUBSCRIBE = "Getting application by id {}";
  private static final String LOG_FIND_BY_ID_SUCCESS = "Retrieved application: {}";
  private static final String LOG_FIND_BY_ID_ERROR =
      "Could not retrieve application for id: {}. Error: {}";

  private static final String LOG_FIND_BY_USER_ID_AND_STATUS_ID_SUBSCRIBE =
      "Getting applications for userId {} and statusId {}";
  private static final String LOG_FIND_BY_USER_ID_AND_STATUS_ID_ERROR =
      "Could not retrieve applications for userId: {} and statusId {}. Error: {}";

  public ApplicationRepositoryAdapter(
      ApplicationReactiveRepository repository,
      ApplicationMapperStandard mapper,
      TransactionalOperator txOperator) {
    super(repository, mapper::toEntity, mapper::toData);
    this.txOperator = txOperator;
  }

  @Override
  public Flux<Application> findAllByUserIdAndApplicationStatusId(
      UUID usrId, UUID applicationStatusId) {
    return repository
        .findAllByUserIdAndApplicationStatusId(usrId, applicationStatusId)
        .map(super::toEntity)
        .as(txOperator::transactional)
        .doOnSubscribe(
            sub ->
                log.info(LOG_FIND_BY_USER_ID_AND_STATUS_ID_SUBSCRIBE, usrId, applicationStatusId))
        .doOnError(
            err ->
                log.error(
                    LOG_FIND_BY_USER_ID_AND_STATUS_ID_ERROR,
                    usrId,
                    applicationStatusId,
                    err.getMessage()));
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
        .doOnSubscribe(sub -> log.info(LOG_SAVE_SUBSCRIBE, entity))
        .doOnSuccess(res -> log.info(LOG_SAVE_SUCCESS, res))
        .doOnError(err -> log.error(LOG_SAVE_ERROR, entity, err.getMessage()));
  }

  @Override
  public Mono<Application> findById(UUID id) {
    return super.findById(id)
        .as(txOperator::transactional)
        .doOnSubscribe(sub -> log.info(LOG_FIND_BY_ID_SUBSCRIBE, id))
        .doOnSuccess(res -> log.info(LOG_FIND_BY_ID_SUCCESS, res))
        .doOnError(err -> log.error(LOG_FIND_BY_ID_ERROR, id, err.getMessage()));
  }
}
