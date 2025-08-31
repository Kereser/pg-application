package co.com.crediya.application.model.security;

import java.util.List;
import java.util.UUID;

public record SecurityDetails(UUID userId, List<String> roles) {}
