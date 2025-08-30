package co.com.crediya.application.r2dbc.helper;

import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class ReactiveAdapterOperations<
    E, D, I, R extends ReactiveCrudRepository<D, I> & ReactiveQueryByExampleExecutor<D>> {
  protected R repository;
  private final Function<D, E> toEntityFn;
  private final Function<E, D> toDomainFn;

  @SuppressWarnings("unchecked")
  protected ReactiveAdapterOperations(
      R repository, Function<D, E> toEntityFn, Function<E, D> toDomainFn) {
    this.repository = repository;
    this.toEntityFn = toEntityFn;
    this.toDomainFn = toDomainFn;
  }

  protected D toData(E entity) {
    return entity != null ? toDomainFn.apply(entity) : null;
  }

  protected E toEntity(D data) {
    return data != null ? toEntityFn.apply(data) : null;
  }

  public Mono<E> save(E entity) {
    return saveData(toData(entity)).map(this::toEntity);
  }

  protected Flux<E> saveAllEntities(Flux<E> entities) {
    return saveData(entities.map(this::toData)).map(this::toEntity);
  }

  protected Mono<D> saveData(D data) {
    return repository.save(data);
  }

  protected Flux<D> saveData(Flux<D> data) {
    return repository.saveAll(data);
  }

  public Mono<E> findById(I id) {
    return repository.findById(id).map(this::toEntity);
  }

  public Flux<E> findByExample(E entity) {
    return repository.findAll(Example.of(toData(entity))).map(this::toEntity);
  }

  public Flux<E> findAll() {
    return repository.findAll().map(this::toEntity);
  }
}
