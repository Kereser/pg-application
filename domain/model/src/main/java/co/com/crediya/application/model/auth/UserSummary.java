package co.com.crediya.application.model.auth;

import java.math.BigDecimal;
import java.util.UUID;

public record UserSummary(
    UUID id,
    String email,
    BigDecimal baseSalary,
    String firstName,
    String idType,
    String idNumber) {}
