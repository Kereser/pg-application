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
        .doOnSubscribe(sub -> log.info("Find product for name: {}", name))
        .doOnSuccess(res -> log.info("Found product type by name: {}. Res: {}", name, res))
        .doOnError(
            err ->
                log.error(
                    "Could not find product type for name: {}. Details: {}",
                    name,
                    err.getMessage()));
  }

  @Override
  public Mono<ProductType> save(ProductType entity) {
    return super.save(entity)
        .as(txOperator::transactional)
        .doOnSubscribe(sub -> log.info("Saving product type {}", entity))
        .doOnSuccess(res -> log.info("Saved product type: {}", res))
        .doOnError(
            err ->
                log.error(
                    "Could not save product type: {}. Details: {}", entity, err.getMessage()));
  }

  @Override
  public Mono<ProductType> findById(UUID id) {
    return super.findById(id)
        .as(txOperator::transactional)
        .doOnSubscribe(sub -> log.info("Fetching productType by id: {}", id))
        .doOnSuccess(res -> log.info("Product type: {} for id: {}", res, id))
        .doOnError(
            err ->
                log.info(
                    "Error while trying to retrieve product type with id: {}. Error: {}",
                    id,
                    err.getMessage()));
  }
}
