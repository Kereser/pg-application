package co.com.crediya.application.model.applicationstatus.gateways;

import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import reactor.core.publisher.Mono;

public interface ApplicationStatusRepository {
  Mono<ApplicationStatus> findPending();
}
