package co.com.crediya.application.usecase.findalltomanualreview;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.application.ApplicationSummary;
import co.com.crediya.application.model.application.dto.GetApplicationFilteredCommand;
import co.com.crediya.application.model.application.gateways.ApplicationRepository;
import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.applicationstatus.ApplicationStatusName;
import co.com.crediya.application.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.auth.gateway.AuthGateway;
import co.com.crediya.application.model.dto.PageDTOResponse;
import co.com.crediya.application.model.exceptions.Entities;
import co.com.crediya.application.model.exceptions.EntityNotFoundException;
import co.com.crediya.application.model.exceptions.TemplateErrors;
import co.com.crediya.application.model.mapper.ApplicationMapper;
import co.com.crediya.application.model.producttype.ProductType;
import co.com.crediya.application.model.producttype.gateways.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FindAllToManualReviewUseCaseImp implements FindAllToManualReviewUseCase {
  private final ApplicationRepository applicationRepository;
  private final ApplicationStatusRepository applicationStatusRepository;
  private final ProductTypeRepository productTypeRepository;
  private final AuthGateway authGateway;
  private final ApplicationMapper mapper;

  private static final List<ApplicationStatusName> REQUIRES_MANUAL_REVISION =
      List.of(
          ApplicationStatusName.PENDING,
          ApplicationStatusName.REJECTED,
          ApplicationStatusName.MANUAL_REVISION);

  private record CoreData(List<Application> applications, Long totalCount) {}

  private record EnrichedData(
      List<Application> applications, Long totalCount, Map<UUID, UserSummary> users) {}

  @Override
  public Mono<PageDTOResponse<ApplicationSummary>> execute(GetApplicationFilteredCommand command) {

    return injectManualStatusFilter(command)
        .flatMap(this::fetchCoreData)
        .flatMap(this::fetchEnrichmentData)
        .map(data -> this.buildFinalResponse(data, command));
  }

  private Mono<GetApplicationFilteredCommand> injectManualStatusFilter(
      GetApplicationFilteredCommand originalCommand) {
    return findManualStatusIds()
        .map(
            ids -> {
              GetApplicationFilteredCommand.ApplicationFilters updatedFilters =
                  originalCommand.getFilters().toBuilder()
                      .productTypeIds(new HashSet<>(ids))
                      .build();
              return originalCommand.toBuilder().filters(updatedFilters).build();
            });
  }

  private Mono<CoreData> fetchCoreData(GetApplicationFilteredCommand updatedCommand) {
    return Mono.zip(
            applicationRepository.findAllFiltered(updatedCommand).collectList(),
            countByFilters(updatedCommand))
        .map(tuple -> new CoreData(tuple.getT1(), tuple.getT2()));
  }

  private Mono<EnrichedData> fetchEnrichmentData(CoreData core) {
    if (core.applications().isEmpty()) {
      return Mono.just(
          new EnrichedData(core.applications(), core.totalCount(), Collections.emptyMap()));
    }

    Set<UUID> userIds = extractIdsFromEntities(core.applications(), Application::getUserId);

    return Mono.zip(
            enrichAppList(core.applications()),
            buildIdEntityMapFromFlux(authGateway.findUsersByIdIn(userIds), UserSummary::id))
        .map(tuple -> new EnrichedData(tuple.getT1(), core.totalCount(), tuple.getT2()));
  }

  private PageDTOResponse<ApplicationSummary> buildFinalResponse(
      EnrichedData allData, GetApplicationFilteredCommand command) {

    List<Application> enrichedApps = allData.applications();
    Long totalCount = allData.totalCount();
    Map<UUID, UserSummary> usersMap = allData.users();

    List<ApplicationSummary> summaryList =
        enrichedApps.stream()
            .map(app -> mapper.toSummary(app, usersMap.get(app.getUserId())))
            .toList();

    return new PageDTOResponse<>(totalCount, command.getPage(), command.getSize(), summaryList);
  }

  private Mono<Long> countByFilters(GetApplicationFilteredCommand filters) {
    return applicationRepository.countByFilers(filters);
  }

  private Mono<List<UUID>> findManualStatusIds() {
    return applicationStatusRepository
        .findAllByNameIn(REQUIRES_MANUAL_REVISION)
        .map(ApplicationStatus::getId)
        .collectList();
  }

  private Mono<Application> enrichApplication(Application application) {
    Mono<ApplicationStatus> statusMono =
        applicationStatusRepository
            .findById(application.getApplicationStatus().getId())
            .switchIfEmpty(
                Mono.error(
                    new EntityNotFoundException(
                        Entities.APPLICATION_STATUS.name(),
                        TemplateErrors.X_NOT_FOUND_FOR_Y.buildMsg(
                            Entities.APPLICATION_STATUS.name(),
                            application.getApplicationStatus().getId()))));

    Mono<ProductType> productTypeMono =
        productTypeRepository
            .findById(application.getProductType().getId())
            .switchIfEmpty(
                Mono.error(
                    new EntityNotFoundException(
                        Entities.PRODUCT_TYPE.name(),
                        TemplateErrors.X_NOT_FOUND_FOR_Y.buildMsg(
                            Entities.PRODUCT_TYPE.name(), application.getProductType().getId()))));

    return statusMono
        .zipWith(productTypeMono)
        .map(
            tuple ->
                application.toBuilder()
                    .applicationStatus(tuple.getT1())
                    .productType(tuple.getT2())
                    .build());
  }

  private <T> Mono<Map<UUID, T>> buildIdEntityMapFromFlux(
      Flux<T> entityList, Function<T, UUID> transformer) {

    return entityList.collect(
        Collectors.toMap(transformer, Function.identity(), (existing, replacement) -> existing));
  }

  private <T> Set<UUID> extractIdsFromEntities(List<T> application, Function<T, UUID> transformer) {
    return application.stream().map(transformer).collect(Collectors.toSet());
  }

  private Mono<List<Application>> enrichAppList(List<Application> list) {
    return Flux.fromIterable(list).flatMap(this::enrichApplication).collectList();
  }
}
