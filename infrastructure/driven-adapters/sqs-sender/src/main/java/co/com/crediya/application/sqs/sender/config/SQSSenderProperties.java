package co.com.crediya.application.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import co.com.crediya.application.model.CommonConstants;

@ConfigurationProperties(prefix = CommonConstants.ConfigProperties.ADAPTER_SQS)
public record SQSSenderProperties(
    String region, String notificationsQueue, String debtEvaluationQueue, String endpoint) {}
