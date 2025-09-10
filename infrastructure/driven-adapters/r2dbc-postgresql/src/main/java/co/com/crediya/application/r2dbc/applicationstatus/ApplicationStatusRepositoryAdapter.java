package co.com.crediya.application.r2dbc.applicationstatus;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;

import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.applicationstatus.ApplicationStatusName;
import co.com.crediya.application.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.crediya.application.r2dbc.applicationstatus.mapper.ApplicationStatusMapperStandard;
import co.com.crediya.application.r2dbc.entity.ApplicationStatusEntity;
import co.com.crediya.application.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class ApplicationStatusRepositoryAdapter
    extends ReactiveAdapterOperations<
        ApplicationStatus, ApplicationStatusEntity, UUID, ApplicationStatusReactiveRepository>
    implements ApplicationStatusRepository {
  private final TransactionalOperator txOperator;

  private static final String LOG_FIND_BY_NAME_SUBSCRIBE =
      "Getting application status obj for name: {}";
  private static final String LOG_FIND_BY_NAME_SUCCESS =
      "Retrieved application status: {}. By name: {}";
  private static final String LOG_FIND_BY_NAME_ERROR =
      "Could not find application type for name {}. Error: {}";

  private static final String LOG_SAVE_SUBSCRIBE = "Saving status {}";
  private static final String LOG_SAVE_SUCCESS = "Saved status: {}";
  private static final String LOG_SAVE_ERROR = "Could not save application status: {}. Details: {}";

  public ApplicationStatusRepositoryAdapter(
      ApplicationStatusReactiveRepository repository,
      ApplicationStatusMapperStandard mapper,
      TransactionalOperator txOperator) {
    super(repository, mapper::toEntity, mapper::toData);
    this.txOperator = txOperator;
  }

  @Override
  public Flux<ApplicationStatus> findAllByNameIn(List<ApplicationStatusName> nameList) {
    List<String> nameStrList = nameList.stream().map(String::valueOf).toList();

    return repository
        .findAllByNameIn(nameStrList)
        .map(super::toEntity)
        .as(txOperator::transactional);
  }

  @Override
  public Mono<ApplicationStatus> findByName(ApplicationStatusName statusName) {
    return repository
        .findByName(statusName.getName())
        .map(super::toEntity)
        .as(txOperator::transactional)
        .doOnSubscribe(sub -> log.info(LOG_FIND_BY_NAME_SUBSCRIBE, statusName))
        .doOnSuccess(res -> log.info(LOG_FIND_BY_NAME_SUCCESS, res, statusName))
        .doOnError(err -> log.error(LOG_FIND_BY_NAME_ERROR, statusName, err.getMessage()));
  }

  @Override
  public Mono<ApplicationStatus> save(ApplicationStatus entity) {
    return super.save(entity)
        .as(txOperator::transactional)
        .doOnSubscribe(sub -> log.info(LOG_SAVE_SUBSCRIBE, entity))
        .doOnSuccess(res -> log.info(LOG_SAVE_SUCCESS, res))
        .doOnError(err -> log.error(LOG_SAVE_ERROR, entity, err.getMessage()));
  }
}
