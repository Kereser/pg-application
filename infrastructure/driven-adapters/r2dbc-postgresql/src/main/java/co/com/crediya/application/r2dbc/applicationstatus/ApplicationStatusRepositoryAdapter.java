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
        .doOnSubscribe(sub -> log.info("Getting pending application status obj"))
        .doOnSuccess(res -> log.info("Pending application status: {}", res))
        .doOnError(
            err ->
                log.error(
                    "Could not find 'PENDING' application type. Details: {}", err.getMessage()));
  }

  @Override
  public Mono<ApplicationStatus> save(ApplicationStatus entity) {
    return super.save(entity)
        .as(txOperator::transactional)
        .doOnSubscribe(sub -> log.info("Saving status {}", entity))
        .doOnSuccess(res -> log.info("saved status: {}", res))
        .doOnError(
            err ->
                log.error(
                    "Could not find application status: {}. Details: {}",
                    entity,
                    err.getMessage()));
  }
}
