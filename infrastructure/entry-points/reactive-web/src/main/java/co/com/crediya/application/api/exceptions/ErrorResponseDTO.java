package co.com.crediya.application.api.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponseDTO(
    @Schema(description = "Attribute or field", example = "email") String attribute,
    @Schema(description = "Http status code", example = "409") int status,
    @Schema(description = "Generic error for class", example = "Field validation exception")
        String error,
    @Schema(
            description = "Detailed error",
            example = "exampleValue is not a valid value for exampleAttribute")
        String details) {}
