package co.com.crediya.application.consumer;

import java.math.BigDecimal;
import java.util.UUID;

public record UserSummaryDTOResponse(
    UUID id,
    String email,
    BigDecimal baseSalary,
    String firstName,
    String idType,
    String idNumber) {}
