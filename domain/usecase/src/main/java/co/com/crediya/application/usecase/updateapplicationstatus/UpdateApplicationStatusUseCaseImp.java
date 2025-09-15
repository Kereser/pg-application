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

  private record OperationData(
      Application application,
      ApplicationStatus newStatus,
      ApplicationStatus approvedStatus,
      Application updatedApplication,
      UserSummary user) {

    OperationData(Application app, ApplicationStatus newStatus, ApplicationStatus approvedStatus) {
      this(app, newStatus, approvedStatus, null, null);
    }

    OperationData withUpdatedApplication(Application app) {
      return new OperationData(
          this.application, this.newStatus, this.approvedStatus, app, this.user);
    }

    OperationData withUser(UserSummary user) {
      return new OperationData(
          this.application, this.newStatus, this.approvedStatus, this.updatedApplication, user);
    }
  }

  @Override
  public Mono<Void> execute(UpdateApplicationStatusCommand command) {
    return preFetch(command)
        .flatMap(this::validateStatusChange)
        .flatMap(this::saveAppWithNewStatus)
        .flatMap(this::performPostSaveActions)
        .flatMap(this::publishEvents)
        .then();
  }

  private Mono<OperationData> preFetch(UpdateApplicationStatusCommand command) {
    return Mono.zip(
            findApplicationById(command.applicationId()),
            findApplicationStatusByName(command.newStatus()),
            findApplicationStatusByName(ApplicationStatusName.APPROVED.getName()))
        .map(tuple -> new OperationData(tuple.getT1(), tuple.getT2(), tuple.getT3()));
  }

  private Mono<OperationData> performPostSaveActions(OperationData data) {
    Mono<Application> enrichedAppMono = this.enrichApplication(data.updatedApplication());
    Mono<UserSummary> userMono = findUser(data.updatedApplication().getUserId());

    return Mono.zip(enrichedAppMono, userMono)
        .map(
            tuple -> {
              Application enrichedApp = tuple.getT1();
              UserSummary user = tuple.getT2();

              return data.withUpdatedApplication(enrichedApp).withUser(user);
            });
  }

  private Mono<Void> publishEvents(OperationData data) {
    Mono<Void> statusUpdateEvent =
        Mono.defer(
            () ->
                notificationEventPublisher.publishStatusUpdate(
                    mapper.toSqsSummary(data.updatedApplication(), data.user())));

    boolean isApproved = data.newStatus().getId().equals(data.approvedStatus().getId());

    if (isApproved) {
      Mono<Void> approvedEvent =
          Mono.defer(
              () ->
                  notificationEventPublisher.publishApprovedApplication(
                      mapper.toSummary(data.updatedApplication())));

      return Mono.when(statusUpdateEvent, approvedEvent);
    } else {
      return statusUpdateEvent;
    }
  }

  private Mono<OperationData> saveAppWithNewStatus(OperationData data) {
    Application appToSave =
        data.application().toBuilder().applicationStatus(data.newStatus()).build();

    return applicationRepository.save(appToSave).map(data::withUpdatedApplication);
  }

  private Mono<OperationData> validateStatusChange(OperationData data) {
    if (data.application().getApplicationStatus().getId().equals(data.newStatus().getId())) {
      return Mono.error(
          new DuplicatedInfoException(
              Entities.APPLICATION_STATUS.name(),
              TemplateErrors.X_ALREADY_ASSIGNED_FOR_Y.buildMsg(
                  data.newStatus().getName(), data.application().getId())));
    }
    return Mono.just(data);
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
