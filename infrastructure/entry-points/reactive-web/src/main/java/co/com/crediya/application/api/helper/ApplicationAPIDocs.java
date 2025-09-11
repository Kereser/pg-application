package co.com.crediya.application.api.helper;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;

import java.util.function.Consumer;

import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.ErrorResponse;

import co.com.crediya.application.api.dto.CreateApplicationDTORequest;
import co.com.crediya.application.api.dto.UpdateApplicationStatusDTORequest;
import co.com.crediya.application.api.exceptions.ErrorResponseDTO;
import co.com.crediya.application.model.CommonConstants;
import co.com.crediya.application.model.application.ApplicationUserSummary;
import co.com.crediya.application.model.application.dto.ApplicationDTOResponse;
import co.com.crediya.application.model.application.dto.GetApplicationFilteredCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationAPIDocs {
  public static Consumer<Builder> saveApplicationDocs() {
    return builder ->
        builder
            .summary(CommonConstants.Docs.Application.SAVE_SUMMARY)
            .description(CommonConstants.Docs.Application.SAVE_DESCRIPTION)
            .operationId(CommonConstants.Docs.Application.SAVE_OPERATION_ID)
            .requestBody(
                requestBodyBuilder()
                    .required(true)
                    .content(
                        contentBuilder()
                            .mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(
                                schemaBuilder().implementation(CreateApplicationDTORequest.class))))
            .response(
                responseBuilder()
                    .responseCode(HttpStatus.OK.name())
                    .content(
                        contentBuilder()
                            .mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(schemaBuilder().implementation(ApplicationDTOResponse.class))))
            .response(
                responseBuilder()
                    .responseCode(HttpStatus.BAD_REQUEST.name())
                    .content(
                        contentBuilder()
                            .mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(schemaBuilder().implementation(ErrorResponseDTO.class))));
  }

  public static Consumer<Builder> getApplicationsForManualReview() {
    return builder ->
        builder
            .summary(CommonConstants.Docs.Application.GET_FOR_MANUAL_REVIEW_SUMMARY)
            .description(CommonConstants.Docs.Application.GET_FOR_MANUAL_REVIEW_DESCRIPTION)
            .operationId(CommonConstants.Docs.Application.GET_FOR_MANUAL_REVIEW_OPERATION_ID)
            .requestBody(
                requestBodyBuilder()
                    .required(true)
                    .content(
                        contentBuilder()
                            .mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(
                                schemaBuilder()
                                    .implementation(GetApplicationFilteredCommand.class))))
            .response(
                responseBuilder()
                    .responseCode(HttpStatus.OK.name())
                    .content(
                        contentBuilder()
                            .mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(schemaBuilder().implementation(ApplicationUserSummary.class))))
            .response(
                responseBuilder()
                    .responseCode(HttpStatus.BAD_REQUEST.name())
                    .content(
                        contentBuilder()
                            .mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(schemaBuilder().implementation(ErrorResponseDTO.class))));
  }

  public static Consumer<Builder> patchApplicationStatus() {
    return builder ->
        builder
            .summary(CommonConstants.Docs.Application.PATCH_UPDATE_STATUS_SUMMARY)
            .description(CommonConstants.Docs.Application.PATCH_UPDATE_STATUS_DESCRIPTION)
            .operationId(CommonConstants.Docs.Application.PATCH_UPDATE_STATUS_OPERATION_ID)
            .requestBody(
                requestBodyBuilder()
                    .required(true)
                    .content(
                        contentBuilder()
                            .mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(
                                schemaBuilder()
                                    .implementation(UpdateApplicationStatusDTORequest.class))))
            .response(responseBuilder().responseCode(HttpStatus.NO_CONTENT.name()))
            .response(
                responseBuilder()
                    .responseCode(HttpStatus.BAD_REQUEST.name())
                    .content(
                        contentBuilder()
                            .mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(schemaBuilder().implementation(ErrorResponseDTO.class))))
            .response(
                responseBuilder()
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.name())
                    .content(
                        contentBuilder()
                            .mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(schemaBuilder().implementation(ErrorResponse.class))));
  }
}
