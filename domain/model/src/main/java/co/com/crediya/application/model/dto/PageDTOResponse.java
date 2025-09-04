package co.com.crediya.application.model.dto;

import java.util.List;

public record PageDTOResponse<T>(long total, int page, int size, List<T> values) {}
