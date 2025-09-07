package co.com.crediya.application.usecase.updateapplicationstatus;

import java.util.Set;
import java.util.UUID;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.application.gateways.ApplicationRepository;
import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.applicationstatus.ApplicationStatusName;
import co.com.crediya.application.model.applicationstatus.dto.UpdateApplicationStatusCommand;
import co.com.crediya.application.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.auth.gateway.AuthGateway;
import co.com.crediya.application.model.eventpublisher.gateway.NotificationEventPublisher;
import co.com.crediya.application.model.exceptions.*;
import co.com.crediya.application.model.mapper.ApplicationMapper;
import co.com.crediya.application.model.producttype.ProductType;
import co.com.crediya.application.model.producttype.gateways.ProductTypeRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class UpdateApplicationStatusUseCaseImp implements UpdateApplicationStatusUseCase {
  private final NotificationEventPublisher notificationEventPublisher;
  private final ApplicationStatusRepository applicationStatusRepository;
  private final ApplicationRepository applicationRepository;
  private final ProductTypeRepository productTypeRepository;
  private final AuthGateway authClient;

  private final ApplicationMapper mapper;

  private record FlowData(Application application, ApplicationStatus applicationStatus) {}

  private record SqsSummaryData(Application application, UserSummary userSummary) {}

  @Override
  public Mono<Void> execute(UpdateApplicationStatusCommand command) {
    return preFetch(command)
        .map(res -> assertNewStatus(res, command))
        .flatMap(this::saveNewAppStatus)
        .flatMap(this::enrichApplication)
        .flatMap(app -> this.findUser(app.getUserId()).map(usr -> new SqsSummaryData(app, usr)))
        .map(summaryData -> mapper.toSqsSummary(summaryData.application, summaryData.userSummary))
        .flatMap(notificationEventPublisher::publishStatusUpdate)
        .then();
  }

  private Mono<FlowData> preFetch(UpdateApplicationStatusCommand command) {
    return Mono.zip(
            findApplicationById(command.applicationId()),
            findApplicationStatusByName(command.newStatus()))
        .map(tuple -> new FlowData(tuple.getT1(), tuple.getT2()));
  }

  private Mono<Application> saveNewAppStatus(FlowData data) {
    Application appToSave =
        data.application().toBuilder().applicationStatus(data.applicationStatus()).build();

    return applicationRepository.save(appToSave);
  }

  private FlowData assertNewStatus(FlowData data, UpdateApplicationStatusCommand command) {
    if (data.application()
        .getApplicationStatus()
        .getId()
        .equals(data.applicationStatus().getId())) {
      throw new DuplicatedInfoException(
          Entities.APPLICATION_STATUS.name(),
          TemplateErrors.X_ALREADY_ASSIGNED_FOR_Y.buildMsg(
              command.newStatus(), command.applicationId()));
    }

    return data;
  }

  private Mono<Application> findApplicationById(UUID applicationId) {
    return applicationRepository
        .findById(applicationId)
        .switchIfEmpty(
            Mono.error(
                new EntityNotFoundException(
                    Entities.APPLICATION.name(),
                    TemplateErrors.X_NOT_FOUND_FOR_Y.buildMsg(
                        Entities.APPLICATION.name(), applicationId))));
  }

  private Mono<ApplicationStatus> findApplicationStatusByName(String appStatusName) {
    return applicationStatusRepository
        .findByName(ApplicationStatusName.fromName(appStatusName))
        .switchIfEmpty(
            Mono.error(
                new EntityNotFoundException(
                    Entities.APPLICATION_STATUS.name(),
                    TemplateErrors.X_NOT_FOUND_FOR_Y.buildMsg(
                        Entities.APPLICATION_STATUS.name(), appStatusName))));
  }

  private Mono<UserSummary> findUser(UUID userId) {
    return authClient
        .findUsersByIdIn(Set.of(userId))
        .filter(usr -> usr.id().equals(userId))
        .switchIfEmpty(
            Mono.error(
                new EntityNotFoundException(
                    Entities.USER.name(),
                    TemplateErrors.X_NOT_FOUND_FOR_Y.buildMsg(Entities.USER.name(), userId))))
        .single();
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
}
