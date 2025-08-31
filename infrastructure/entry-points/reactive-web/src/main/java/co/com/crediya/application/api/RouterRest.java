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
                      responseCode = "200",
                      description = "Application created",
                      content =
                          @Content(
                              schema = @Schema(implementation = ApplicationDTOResponse.class))),
                  @ApiResponse(
                      responseCode = "400",
                      description = "Invalid input",
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema = @Schema(implementation = ErrorResponseDTO.class))),
                  @ApiResponse(
                      responseCode = "409",
                      description = "Conflict",
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema = @Schema(implementation = ErrorResponseDTO.class))),
                  @ApiResponse(
                      responseCode = "500",
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
            routes.getPaths().getBase(),
            builder ->
                builder.POST(
                    routes.getPaths().getApplication(), applicationHandler::listenSaveApplication))
        .build();
  }
}
