package co.com.crediya.application.consumer;

public record BusinessExceptionResponse(
    String attribute, int status, String error, String details) {}
