package co.com.crediya.application.r2dbc.application;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import co.com.crediya.application.model.dto.GetApplicationFilteredCommand;
import co.com.crediya.application.r2dbc.entity.ApplicationEntity;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ApplicationTemplateQueryRepositoryImpl implements ApplicationTemplateQueryRepository {

  private final R2dbcEntityTemplate entityTemplate;

  static class DbFields {
    private DbFields() {}

    private static final String USER_ID = "user_id";
    private static final String AMOUNT = "amount";
    private static final String PRODUCT_NAME_ID = "product_type_id";
  }

  @Override
  public Flux<ApplicationEntity> findAllFiltered(GetApplicationFilteredCommand filters) {
    Criteria criteria = buildCriteria(filters);

    int limit = filters.getSize();
    int offset = filters.getPage() * filters.getSize();

    Query query = Query.query(criteria).limit(limit).offset(offset);

    return entityTemplate.select(query, ApplicationEntity.class);
  }

  @Override
  public Mono<Long> countAllFiltered(GetApplicationFilteredCommand filters) {
    Criteria criteria = buildCriteria(filters);

    return entityTemplate.count(Query.query(criteria), ApplicationEntity.class);
  }

  private Criteria buildCriteria(GetApplicationFilteredCommand filters) {
    Criteria criteria = Criteria.empty();

    if (filters.getFilters().getUserId() != null) {
      criteria = criteria.and(DbFields.USER_ID).is(filters.getFilters().getUserId());
    }

    if (filters.getFilters().getAmount() != null) {
      criteria =
          criteria.and(DbFields.AMOUNT).greaterThanOrEquals(filters.getFilters().getAmount());
    }

    if (filters.getFilters().getHaveManualReview() != null
        && filters.getFilters().getProductTypeIds() != null
        && !filters.getFilters().getProductTypeIds().isEmpty()) {
      criteria =
          criteria.and(DbFields.PRODUCT_NAME_ID).in(filters.getFilters().getProductTypeIds());
    }

    return criteria;
  }
}
