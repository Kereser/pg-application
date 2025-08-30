package co.com.crediya.application.model.dto;

import java.math.BigDecimal;

public record CreateApplicationCommand(
    String idNumber, BigDecimal amount, Integer applicationPeriod, String productName) {}
