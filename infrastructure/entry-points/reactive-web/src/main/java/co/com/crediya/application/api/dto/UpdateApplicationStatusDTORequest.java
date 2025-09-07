package co.com.crediya.application.api.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Pattern;

public record UpdateApplicationStatusDTORequest(
    @Nonnull @Pattern(regexp = "APPROVED|REJECTED") String newStatus) {}
