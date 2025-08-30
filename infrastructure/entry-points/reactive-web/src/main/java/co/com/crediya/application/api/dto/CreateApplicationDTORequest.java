package co.com.crediya.application.api.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateApplicationDTORequest(
        @Nonnull String idNumber,
        @Nonnull BigDecimal amount,
        @Nonnull Integer applicationPeriod,
        @Nonnull @NotBlank String productName) {}
