package co.com.crediya.application.r2dbc.application;

import java.util.UUID;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.crediya.application.r2dbc.entity.ApplicationEntity;

public interface ApplicationReactiveRepository
    extends ReactiveCrudRepository<ApplicationEntity, UUID>,
        ReactiveQueryByExampleExecutor<ApplicationEntity> {}
