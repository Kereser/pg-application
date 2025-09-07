package co.com.crediya.application.api.exceptions;

import co.com.crediya.application.api.helper.RestConstants;
import co.com.crediya.application.model.CommonConstants;
import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponseDTO(
    @Schema(
            description = CommonConstants.ErrorResponse.ATTRIBUTE_DESCRIPTION,
            example = CommonConstants.Fields.EMAIL)
        String attribute,
    @Schema(
            description = CommonConstants.ErrorResponse.STATUS_DESCRIPTION,
            example = RestConstants.StatusCodeInt.CONFLICT)
        int status,
    @Schema(
            description = CommonConstants.ErrorResponse.ERROR_DESCRIPTION,
            example = CommonConstants.ErrorResponse.ERROR_EXAMPLE)
        String error,
    @Schema(
            description = CommonConstants.ErrorResponse.DETAILS_DESCRIPTION,
            example = CommonConstants.ErrorResponse.DETAILS_EXAMPLE)
        String details) {}
