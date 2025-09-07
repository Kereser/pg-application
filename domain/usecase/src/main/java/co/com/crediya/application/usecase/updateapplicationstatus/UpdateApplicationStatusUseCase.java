package co.com.crediya.application.usecase.updateapplicationstatus;

import co.com.crediya.application.model.applicationstatus.dto.UpdateApplicationStatusCommand;
import reactor.core.publisher.Mono;

public interface UpdateApplicationStatusUseCase {

  Mono<Void> execute(UpdateApplicationStatusCommand command);
}
