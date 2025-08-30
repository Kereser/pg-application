package co.com.crediya.application.r2dbc.producttype;

import java.util.UUID;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.crediya.application.r2dbc.entity.ProductTypeEntity;
import reactor.core.publisher.Mono;

public interface ProductTypeReactiveRepository
    extends ReactiveCrudRepository<ProductTypeEntity, UUID>,
        ReactiveQueryByExampleExecutor<ProductTypeEntity> {
  Mono<ProductTypeEntity> findByName(String name);
}
