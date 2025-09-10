package co.com.crediya.application.r2dbc.application;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.crediya.application.r2dbc.entity.ApplicationEntity;
import reactor.core.publisher.Flux;

public interface ApplicationReactiveRepository
    extends ReactiveCrudRepository<ApplicationEntity, UUID>,
        ReactiveQueryByExampleExecutor<ApplicationEntity>,
        ApplicationTemplateQueryRepository {

  @Query("SELECT * FROM applications WHERE user_id = :userId AND application_status_id = :statusId")
  Flux<ApplicationEntity> findAllByUserIdAndApplicationStatusId(UUID userId, UUID statusId);
}
