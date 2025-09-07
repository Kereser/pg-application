package co.com.crediya.application.model.sqs;

import java.util.UUID;

import co.com.crediya.application.model.applicationstatus.ApplicationStatusName;

public record SqsSummaryDTO(
    UUID applicationId, String email, String name, ApplicationStatusName status) {}
