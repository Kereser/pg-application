package co.com.crediya.application.model.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ApplicationDTOResponse(
    UUID id, BigDecimal amount, UUID userId, UUID statusId, UUID productTypeId) {}
