package co.com.crediya.application.model.eventpublisher.dto;

import java.util.List;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.auth.UserSummary;

public record DebtEvaluationDTO(UserSummary usr, List<Application> applications) {}
