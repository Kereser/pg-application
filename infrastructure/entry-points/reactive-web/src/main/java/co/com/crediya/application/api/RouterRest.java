package co.com.crediya.application.api;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.application.api.config.Routes;
import co.com.crediya.application.api.dto.CreateApplicationDTORequest;
import co.com.crediya.application.api.exceptions.ErrorResponseDTO;
import co.com.crediya.application.api.helper.RestConstants;
import co.com.crediya.application.model.dto.ApplicationDTOResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class RouterRest {
  private final Routes routes;
  private final Handler applicationHandler;

  @Bean
  @RouterOperations({
    @RouterOperation(
        path = "api/v1/applications",
        produces = {"application/json"},
        method = RequestMethod.POST,
        beanClass = Handler.class,
        beanMethod = "listenSaveApplication",
        operation =
            @Operation(
                operationId = "createApplication",
                summary = "Create a new application",
                requestBody =
                    @RequestBody(
                        required = true,
                        description = "Application creation request",
                        content =
                            @Content(
                                schema =
                                    @Schema(implementation = CreateApplicationDTORequest.class))),
                responses = {
                  @ApiResponse(
                      responseCode = RestConstants.StatusCodeInt.OK,
                      description = "Application created",
                      content =
                          @Content(
                              schema = @Schema(implementation = ApplicationDTOResponse.class))),
                  @ApiResponse(
                      responseCode = RestConstants.StatusCodeInt.BAD_REQUEST,
                      description = "Invalid input",
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema = @Schema(implementation = ErrorResponseDTO.class))),
                  @ApiResponse(
                      responseCode = RestConstants.StatusCodeInt.CONFLICT,
                      description = "Conflict",
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema = @Schema(implementation = ErrorResponseDTO.class))),
                  @ApiResponse(
                      responseCode = RestConstants.StatusCodeInt.SERVER_ERROR,
                      description = "Internal Server Error",
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema = @Schema(implementation = ErrorResponse.class)))
                })),
    @RouterOperation(
        path = RestConstants.ApplicationAPI.REVIEW,
        produces = {"application/json"},
        method = RequestMethod.GET,
        beanClass = Handler.class,
        beanMethod = "listenGetApplicationsForManualReview",
        operation =
            @Operation(
                operationId = "getApplicationsForManualReview",
                summary = "Get applications pending manual review",
                parameters = {
                  @io.swagger.v3.oas.annotations.Parameter(
                      name = "page",
                      description = "Page number",
                      schema = @Schema(type = "integer", defaultValue = "0")),
                  @io.swagger.v3.oas.annotations.Parameter(
                      name = "size",
                      description = "Page size",
                      schema = @Schema(type = "integer", defaultValue = "2")),
                  @io.swagger.v3.oas.annotations.Parameter(
                      name = "userId",
                      description = "User id to filter by",
                      schema =
                          @Schema(
                              type = "string",
                              format = "uuid",
                              example = "550e8400-e29b-41d4-a716-446655440000")),
                  @io.swagger.v3.oas.annotations.Parameter(
                      name = "amount",
                      description = "amount used to filter by",
                      schema =
                          @Schema(type = "number", format = "decimal", example = "1200000.50")),
                },
                responses = {
                  @ApiResponse(
                      responseCode = RestConstants.StatusCodeInt.OK,
                      description = "List of applications",
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema = @Schema(implementation = ApplicationDTOResponse.class))),
                  @ApiResponse(
                      responseCode = RestConstants.StatusCodeInt.SERVER_ERROR,
                      description = "Internal Server Error",
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema = @Schema(implementation = ErrorResponse.class)))
                }))
  })
  public RouterFunction<ServerResponse> routerFunction(Handler handler) {
    return route()
        .path(
            RestConstants.ApplicationAPI.ROOT,
            builder ->
                builder
                    .POST(
                        routes.getPaths().getApplication(),
                        applicationHandler::listenSaveApplication)
                    .GET(
                        routes.getPaths().getApplicationsToReview(),
                        applicationHandler::listenGetApplicationsForManualReview))
        .build();
  }
}
