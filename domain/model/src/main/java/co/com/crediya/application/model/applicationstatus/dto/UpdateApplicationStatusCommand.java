package co.com.crediya.application.model.applicationstatus.dto;

import java.util.UUID;

public record UpdateApplicationStatusCommand(UUID applicationId, String newStatus) {}
