package co.com.crediya.application.sqs.sender;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.eventpublisher.dto.DebtEvaluationDTO;
import co.com.crediya.application.model.eventpublisher.gateway.NotificationEventPublisher;
import co.com.crediya.application.model.sqs.SqsSummaryDTO;
import co.com.crediya.application.sqs.sender.config.SQSSenderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements NotificationEventPublisher {
  private final SQSSenderProperties properties;
  private final SqsAsyncClient client;
  private final ObjectMapper mapper;

  private static final String LOG_STATUS_UPDATE_SUCCESS =
      "Msg sent to update status notification queue. Msg id {}";
  private static final String LOG_STATUS_UPDATE_ERROR = "Unable to send message to SQS. Error: {}";

  private static final String LOG_DEBT_EVALUATION_SUCCESS =
      "Sending msg to debt evaluation. Msg id {}";
  private static final String LOG_DEBT_EVALUATION_ERROR =
      "Unable to send message to debt evaluation queue. Error: {}";

  @Override
  public Mono<Void> publishStatusUpdate(SqsSummaryDTO sqsSummaryDTO) {
    return Mono.fromCallable(
            () -> {
              UpdateStatusEvent event =
                  new UpdateStatusEvent(
                      sqsSummaryDTO.applicationId(),
                      sqsSummaryDTO.email(),
                      sqsSummaryDTO.name(),
                      sqsSummaryDTO.status().getName());

              return mapper.writeValueAsString(event);
            })
        .flatMap(
            msg -> {
              SendMessageRequest req =
                  SendMessageRequest.builder()
                      .queueUrl(properties.notificationsQueue())
                      .messageBody(msg)
                      .build();

              return Mono.fromFuture(client.sendMessage(req));
            })
        .doOnNext(res -> log.info(LOG_STATUS_UPDATE_SUCCESS, res.messageId()))
        .then()
        .doOnError(err -> log.error(LOG_STATUS_UPDATE_ERROR, err.getMessage()));
  }

  @Override
  public Mono<Void> publishDebtEvaluationQueue(DebtEvaluationDTO debtEvaluationDTO) {
    return Mono.fromCallable(
            () -> {
              DebtValidationEvent event =
                  new DebtValidationEvent(
                      debtEvaluationDTO.usr().id(),
                      debtEvaluationDTO.usr().email(),
                      debtEvaluationDTO.usr().firstName(),
                      debtEvaluationDTO.usr().baseSalary(),
                      debtEvaluationDTO.applications());

              return mapper.writeValueAsString(event);
            })
        .flatMap(
            msg -> {
              SendMessageRequest req =
                  SendMessageRequest.builder()
                      .queueUrl(properties.debtEvaluationQueue())
                      .messageBody(msg)
                      .build();

              return Mono.fromFuture(client.sendMessage(req));
            })
        .doOnNext(res -> log.info(LOG_DEBT_EVALUATION_SUCCESS, res.messageId()))
        .then()
        .doOnError(err -> log.error(LOG_DEBT_EVALUATION_ERROR, err.getMessage()));
  }

  private record UpdateStatusEvent(UUID applicationId, String email, String name, String status) {}

  private record DebtValidationEvent(
      UUID userId,
      String email,
      String name,
      BigDecimal baseSalary,
      List<Application> applications) {}
}
