package co.com.crediya.application.model.producttype.gateways;

import co.com.crediya.application.model.producttype.ProductType;
import reactor.core.publisher.Mono;

public interface ProductTypeRepository {
  Mono<ProductType> findByName(String name);
}
