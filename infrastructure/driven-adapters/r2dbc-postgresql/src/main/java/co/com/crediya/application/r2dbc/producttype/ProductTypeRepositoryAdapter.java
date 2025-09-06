package co.com.crediya.application.r2dbc.producttype;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;

import co.com.crediya.application.model.producttype.ProductType;
import co.com.crediya.application.model.producttype.gateways.ProductTypeRepository;
import co.com.crediya.application.r2dbc.entity.ProductTypeEntity;
import co.com.crediya.application.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.application.r2dbc.producttype.mapper.ProductTypeMapperStandard;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class ProductTypeRepositoryAdapter
    extends ReactiveAdapterOperations<
        ProductType, ProductTypeEntity, UUID, ProductTypeReactiveRepository>
    implements ProductTypeRepository {
  private final TransactionalOperator txOperator;

  private static final String LOG_FIND_BY_NAME_SUBSCRIBE = "Find product for name: {}";
  private static final String LOG_FIND_BY_NAME_SUCCESS = "Found product type by name: {}. Res: {}";
  private static final String LOG_FIND_BY_NAME_ERROR =
      "Could not find product type for name: {}. Details: {}";

  private static final String LOG_SAVE_SUBSCRIBE = "Saving product type {}";
  private static final String LOG_SAVE_SUCCESS = "Saved product type: {}";
  private static final String LOG_SAVE_ERROR = "Could not save product type: {}. Details: {}";

  private static final String LOG_FIND_BY_ID_SUBSCRIBE = "Fetching productType by id: {}";
  private static final String LOG_FIND_BY_ID_SUCCESS = "Product type: {} for id: {}";
  private static final String LOG_FIND_BY_ID_ERROR =
      "Error while trying to retrieve product type with id: {}. Error: {}";

  public ProductTypeRepositoryAdapter(
      ProductTypeReactiveRepository repository,
      ProductTypeMapperStandard mapper,
      TransactionalOperator txOperator) {
    super(repository, mapper::toEntity, mapper::toData);
    this.txOperator = txOperator;
  }

  @Override
  public Mono<ProductType> findByName(String name) {
    return repository
        .findByName(name)
        .map(super::toEntity)
        .as(txOperator::transactional)
        .doOnSubscribe(sub -> log.info(LOG_FIND_BY_NAME_SUBSCRIBE, name))
        .doOnSuccess(res -> log.info(LOG_FIND_BY_NAME_SUCCESS, name, res))
        .doOnError(err -> log.error(LOG_FIND_BY_NAME_ERROR, name, err.getMessage()));
  }

  @Override
  public Mono<ProductType> save(ProductType entity) {
    return super.save(entity)
        .as(txOperator::transactional)
        .doOnSubscribe(sub -> log.info(LOG_SAVE_SUBSCRIBE, entity))
        .doOnSuccess(res -> log.info(LOG_SAVE_SUCCESS, res))
        .doOnError(err -> log.error(LOG_SAVE_ERROR, entity, err.getMessage()));
  }

  @Override
  public Mono<ProductType> findById(UUID id) {
    return super.findById(id)
        .as(txOperator::transactional)
        .doOnSubscribe(sub -> log.info(LOG_FIND_BY_ID_SUBSCRIBE, id))
        .doOnSuccess(res -> log.info(LOG_FIND_BY_ID_SUCCESS, res, id))
        .doOnError(err -> log.info(LOG_FIND_BY_ID_ERROR, id, err.getMessage()));
  }
}
