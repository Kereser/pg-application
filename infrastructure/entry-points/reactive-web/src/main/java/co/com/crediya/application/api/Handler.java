package co.com.crediya.application.api;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.application.api.dto.ApplicationFiltersDTORequest;
import co.com.crediya.application.api.dto.CreateApplicationDTORequest;
import co.com.crediya.application.api.dto.UpdateApplicationStatusDTORequest;
import co.com.crediya.application.api.helper.ApplicationRestMapper;
import co.com.crediya.application.model.CommonConstants;
import co.com.crediya.application.model.applicationstatus.dto.UpdateApplicationStatusCommand;
import co.com.crediya.application.model.exceptions.Entities;
import co.com.crediya.application.requestvalidator.RequestValidator;
import co.com.crediya.application.usecase.createapplication.CreateApplicationUseCase;
import co.com.crediya.application.usecase.findalltomanualreview.FindAllToManualReviewUseCase;
import co.com.crediya.application.usecase.updateapplicationstatus.UpdateApplicationStatusUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {
  private final CreateApplicationUseCase createApplicationUseCase;
  private final FindAllToManualReviewUseCase findAllToManualReviewUseCase;
  private final UpdateApplicationStatusUseCase updateApplicationStatusUseCase;
  private final RequestValidator reqValidator;
  private final ApplicationRestMapper restMapper;

  private static final String LOG_CREATE_SUBSCRIBE =
      "Starting creation of application with command: {}";
  private static final String LOG_CREATE_SUCCESS = "Successfully created application: {}";
  private static final String LOG_CREATE_ERROR =
      "Error while trying to create application for command: {}, Details: {}";

  private static final String LOG_GET_SUBSCRIBE = "Getting applications";
  private static final String LOG_GET_SUCCESS = "Applications found: {}";
  private static final String LOG_GET_ERROR = "Error while retrieving applications: {}";

  private static final String LOG_UPDATE_STATUS_SUBSCRIBE =
      "Request update status for applicationId: {}";
  private static final String LOG_UPDATE_STATUS_SUCCESS =
      "Status successfully update for applicationId: {}";
  private static final String LOG_UPDATE_STATUS_ERROR =
      "Error while updating status for applicationId: {}";

  public Mono<ServerResponse> listenSaveApplication(ServerRequest req) {
    return reqValidator
        .validate(req, CreateApplicationDTORequest.class, Entities.APPLICATION.name())
        .map(restMapper::toCommand)
        .flatMap(
            cmd ->
                createApplicationUseCase
                    .execute(cmd)
                    .doOnSubscribe(sub -> log.info(LOG_CREATE_SUBSCRIBE, cmd))
                    .doOnSuccess(dto -> log.info(LOG_CREATE_SUCCESS, dto))
                    .doOnError(err -> log.error(LOG_CREATE_ERROR, cmd, err.getMessage())))
        .flatMap(res -> ServerResponse.status(HttpStatus.CREATED).bodyValue(res));
  }

  public Mono<ServerResponse> listenGetApplicationsForManualReview(ServerRequest req) {
    return findAllToManualReviewUseCase
        .execute(restMapper.toCommand(ApplicationFiltersDTORequest.from(req)))
        .doOnSubscribe(sub -> log.info(LOG_GET_SUBSCRIBE))
        .doOnSuccess(res -> log.info(LOG_GET_SUCCESS, res))
        .doOnError(err -> log.error(LOG_GET_ERROR, err.getMessage()))
        .flatMap(res -> ServerResponse.ok().bodyValue(res));
  }

  public Mono<ServerResponse> listenPatchApplicationWithStatus(ServerRequest req) {
    UUID applicationId =
        UUID.fromString(req.pathVariable(CommonConstants.PathVariables.APPLICATION_ID));

    return reqValidator
        .validate(req, UpdateApplicationStatusDTORequest.class, Entities.APPLICATION_STATUS.name())
        .map(dto -> new UpdateApplicationStatusCommand(applicationId, dto.newStatus()))
        .flatMap(updateApplicationStatusUseCase::execute)
        .doOnSubscribe(sub -> log.info(LOG_UPDATE_STATUS_SUBSCRIBE, applicationId))
        .doOnSuccess(res -> log.info(LOG_UPDATE_STATUS_SUCCESS, applicationId))
        .doOnError(err -> log.error(LOG_UPDATE_STATUS_ERROR, err.getMessage()))
        .flatMap(res -> ServerResponse.noContent().build());
  }
}
