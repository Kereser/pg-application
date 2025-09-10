package co.com.crediya.application.sqs.listener;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.crediya.application.model.eventpublisher.dto.DebtEvaluationDTOInput;
import co.com.crediya.application.model.exceptions.GenericBadRequestException;
import co.com.crediya.application.usecase.handlestatusafterlambdadebtevaluation.HandleStatusAfterLambdaDebtEvaluationUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
  private final HandleStatusAfterLambdaDebtEvaluationUseCase useCase;
  private final ObjectMapper objectMapper;

  private static final String ERROR_WHEN_APPLY_PROCESSOR = "Error parsing SQS message ";

  @Override
  public Mono<Void> apply(Message message) {
    try {
      DebtEvaluationDTOInput event =
          objectMapper.readValue(message.body(), DebtEvaluationDTOInput.class);

      return useCase.execute(event);
    } catch (Exception e) {
      return Mono.error(
          new GenericBadRequestException(
              DebtEvaluationDTOInput.class.getSimpleName(),
              ERROR_WHEN_APPLY_PROCESSOR + e.getMessage()));
    }
  }
}
