package co.com.crediya.application.model.applicationstatus.gateways;

import java.util.List;
import java.util.UUID;

import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.applicationstatus.ApplicationStatusName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationStatusRepository {
  Mono<ApplicationStatus> findByName(ApplicationStatusName statusName);

  Mono<ApplicationStatus> findById(UUID id);

  Flux<ApplicationStatus> findAllByNameIn(List<ApplicationStatusName> nameList);
}
