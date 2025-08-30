package co.com.crediya.application.model.auth;

import java.util.UUID;

public record UserSummary(
    UUID id, String email, String firstName, String idType, String idNumber) {}
