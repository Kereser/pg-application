package co.com.crediya.application.model.eventpublisher.gateway;

import co.com.crediya.application.model.sqs.SqsSummaryDTO;
import reactor.core.publisher.Mono;

public interface NotificationEventPublisher {
  Mono<Void> publishStatusUpdate(SqsSummaryDTO sqsSummaryDTO);
}
