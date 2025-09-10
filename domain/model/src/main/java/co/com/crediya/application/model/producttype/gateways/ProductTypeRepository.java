package co.com.crediya.application.model.producttype.gateways;

import java.util.UUID;

import co.com.crediya.application.model.producttype.ProductType;
import co.com.crediya.application.model.producttype.vo.ProductName;
import reactor.core.publisher.Mono;

public interface ProductTypeRepository {
  Mono<ProductType> findByName(ProductName name);

  Mono<ProductType> findById(UUID id);
}
