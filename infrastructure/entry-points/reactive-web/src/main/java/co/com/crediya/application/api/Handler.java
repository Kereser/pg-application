package co.com.crediya.application.api;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.application.api.dto.CreateApplicationDTORequest;
import co.com.crediya.application.api.helper.ApplicationRestMapper;
import co.com.crediya.application.model.exceptions.Entities;
import co.com.crediya.application.requestvalidator.RequestValidator;
import co.com.crediya.application.usecase.createapplication.CreateApplicationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {
  private final CreateApplicationUseCase createApplicationUseCase;
  private final RequestValidator reqValidator;
  private final ApplicationRestMapper restMapper;

  public Mono<ServerResponse> listenSaveApplication(ServerRequest req) {

    return reqValidator
        .validate(req, CreateApplicationDTORequest.class, Entities.APPLICATION.name())
        .map(restMapper::toCommand)
        .flatMap(
            cmd ->
                createApplicationUseCase
                    .execute(cmd)
                    .doOnSubscribe(
                        sub -> log.info("Starting creation of application with command: {}", cmd))
                    .doOnSuccess(dto -> log.info("Successfully created application: {}", dto))
                    .doOnError(
                        err ->
                            log.error(
                                "Error while trying to create application for command: {}, Details: {}",
                                cmd,
                                err.getMessage())))
        .flatMap(res -> ServerResponse.ok().bodyValue(res));
  }
}
