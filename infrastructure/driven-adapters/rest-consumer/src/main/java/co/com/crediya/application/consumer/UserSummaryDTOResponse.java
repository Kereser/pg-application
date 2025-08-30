package co.com.crediya.application.consumer;

import java.util.UUID;

public record UserSummaryDTOResponse(
    UUID id, String email, String firstName, String idType, String idNumber) {}
