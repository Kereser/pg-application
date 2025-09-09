package co.com.crediya.application.usecase.handlestatusafterlambdadebtevaluation;

import co.com.crediya.application.model.application.gateways.ApplicationRepository;
import co.com.crediya.application.model.applicationstatus.ApplicationStatusName;
import co.com.crediya.application.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.crediya.application.model.eventpublisher.dto.DebtEvaluationDTOInput;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class HandleStatusAfterLambdaDebtEvaluationUseCaseImp
    implements HandleStatusAfterLambdaDebtEvaluationUseCase {

  private final ApplicationRepository applicationRepository;
  private final ApplicationStatusRepository applicationStatusRepository;

  @Override
  public Mono<Void> execute(DebtEvaluationDTOInput dto) {
    return Mono.zip(
            applicationRepository.findById(dto.getPayload().getApplicationId()),
            applicationStatusRepository.findByName(
                ApplicationStatusName.fromName(dto.getPayload().getNewStatus())))
        .flatMap(
            tuple ->
                applicationRepository.save(
                    tuple.getT1().toBuilder().applicationStatus(tuple.getT2()).build()))
        .then();
  }
}
