package co.com.crediya.application.api.dto;

import java.math.BigDecimal;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

public record CreateApplicationDTORequest(
    @Nonnull String idNumber,
    @Nonnull BigDecimal amount,
    @Nonnull Integer applicationPeriod,
    @Nonnull @NotBlank String productName) {}
