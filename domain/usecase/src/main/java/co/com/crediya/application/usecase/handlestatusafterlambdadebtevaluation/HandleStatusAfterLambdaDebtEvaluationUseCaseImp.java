package co.com.crediya.application.usecase.handlestatusafterlambdadebtevaluation;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.application.gateways.ApplicationRepository;
import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.applicationstatus.ApplicationStatusName;
import co.com.crediya.application.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.crediya.application.model.eventpublisher.dto.DebtEvaluationDTOInput;
import co.com.crediya.application.model.eventpublisher.gateway.NotificationEventPublisher;
import co.com.crediya.application.model.mapper.ApplicationMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class HandleStatusAfterLambdaDebtEvaluationUseCaseImp
    implements HandleStatusAfterLambdaDebtEvaluationUseCase {

  private final ApplicationRepository applicationRepository;
  private final ApplicationStatusRepository applicationStatusRepository;
  private final NotificationEventPublisher notificationEventPublisher;
  private final ApplicationMapper applicationMapper;

  private record OperationData(
      Application application, ApplicationStatus incomingStatus, ApplicationStatus approvedStatus) {
    OperationData withApplication(Application newApplication) {
      return new OperationData(newApplication, this.incomingStatus, this.approvedStatus);
    }
  }

  @Override
  public Mono<Void> execute(DebtEvaluationDTOInput dto) {

    return preFetch(dto)
        .flatMap(this::saveApplicationWithNewStatus)
        .flatMap(this::publishApprovedEventIfRequired)
        .then();
  }

  private Mono<OperationData> saveApplicationWithNewStatus(OperationData data) {
    Application appToSave =
        data.application().toBuilder().applicationStatus(data.incomingStatus()).build();

    return applicationRepository.save(appToSave).map(data::withApplication);
  }

  private Mono<OperationData> preFetch(DebtEvaluationDTOInput dto) {
    Mono<Application> applicationMono =
        applicationRepository.findById(dto.getPayload().getApplicationId());
    Mono<ApplicationStatus> incomingStatus =
        applicationStatusRepository.findByName(
            ApplicationStatusName.fromName(dto.getPayload().getNewStatus()));
    Mono<ApplicationStatus> approvedStatus =
        applicationStatusRepository.findByName(ApplicationStatusName.APPROVED);

    return Mono.zip(applicationMono, incomingStatus, approvedStatus)
        .map(tuple -> new OperationData(tuple.getT1(), tuple.getT2(), tuple.getT3()));
  }

  private Mono<Void> publishApprovedEventIfRequired(OperationData data) {
    boolean isApproved = data.incomingStatus().getId().equals(data.approvedStatus().getId());

    if (!isApproved) {
      return Mono.empty();
    }

    return notificationEventPublisher.publishApprovedApplication(
        applicationMapper.toSummary(data.application()));
  }
}
