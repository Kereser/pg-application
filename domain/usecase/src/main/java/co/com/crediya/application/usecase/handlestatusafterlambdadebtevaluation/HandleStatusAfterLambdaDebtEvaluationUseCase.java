package co.com.crediya.application.usecase.handlestatusafterlambdadebtevaluation;

import co.com.crediya.application.model.eventpublisher.dto.DebtEvaluationDTOInput;
import reactor.core.publisher.Mono;

public interface HandleStatusAfterLambdaDebtEvaluationUseCase {
  Mono<Void> execute(DebtEvaluationDTOInput dto);
}
