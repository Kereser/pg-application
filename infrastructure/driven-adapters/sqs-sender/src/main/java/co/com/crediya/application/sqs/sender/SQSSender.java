package co.com.crediya.application.sqs.sender;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

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

  private static final String LOG_PUBLISH_SUCCESS = "Msg sent {}";
  private static final String LOG_PUBLISH_ERROR = "Unable to send message to SQS. Error: {}";

  @Override
  public Mono<Void> publishStatusUpdate(SqsSummaryDTO sqsSummaryDTO) {
    return Mono.fromCallable(
            () -> {
              NotificationEvent event =
                  new NotificationEvent(
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
                      .queueUrl(properties.queueUrl())
                      .messageBody(msg)
                      .build();

              return Mono.fromFuture(client.sendMessage(req));
            })
        .doOnNext(res -> log.debug(LOG_PUBLISH_SUCCESS, res.messageId()))
        .then()
        .doOnError(err -> log.error(LOG_PUBLISH_ERROR, err.getMessage()));
  }

  private record NotificationEvent(UUID applicationId, String email, String name, String status) {}
}
