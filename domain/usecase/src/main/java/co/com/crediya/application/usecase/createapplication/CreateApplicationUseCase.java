package co.com.crediya.application.usecase.createapplication;

import co.com.crediya.application.model.dto.ApplicationDTOResponse;
import co.com.crediya.application.model.dto.CreateApplicationCommand;
import reactor.core.publisher.Mono;

public interface CreateApplicationUseCase {
  Mono<ApplicationDTOResponse> execute(CreateApplicationCommand command);
}
