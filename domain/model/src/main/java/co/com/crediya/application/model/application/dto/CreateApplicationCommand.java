package co.com.crediya.application.model.application.dto;

import java.math.BigDecimal;

public record CreateApplicationCommand(
    String idNumber, BigDecimal amount, Integer applicationPeriod, String productName) {}
