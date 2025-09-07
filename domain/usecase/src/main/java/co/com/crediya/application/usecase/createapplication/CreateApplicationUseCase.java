package co.com.crediya.application.usecase.createapplication;

import co.com.crediya.application.model.application.dto.ApplicationDTOResponse;
import co.com.crediya.application.model.application.dto.CreateApplicationCommand;
import reactor.core.publisher.Mono;

public interface CreateApplicationUseCase {
  Mono<ApplicationDTOResponse> execute(CreateApplicationCommand command);
}
