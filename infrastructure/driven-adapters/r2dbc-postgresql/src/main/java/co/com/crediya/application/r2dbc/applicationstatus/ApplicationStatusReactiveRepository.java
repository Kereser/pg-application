package co.com.crediya.application.r2dbc.applicationstatus;

import java.util.List;
import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.crediya.application.r2dbc.entity.ApplicationStatusEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationStatusReactiveRepository
    extends ReactiveCrudRepository<ApplicationStatusEntity, UUID>,
        ReactiveQueryByExampleExecutor<ApplicationStatusEntity> {

  @Query("SELECT * FROM application_status a WHERE a.name = :name")
  Mono<ApplicationStatusEntity> findByName(String name);

  Flux<ApplicationStatusEntity> findAllByNameIn(List<String> names);
}
