package co.com.crediya.application.consumer.helper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import co.com.crediya.application.consumer.UserSummaryDTOResponse;
import co.com.crediya.application.model.auth.UserSummary;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthRestMapper {
  UserSummary toDomain(UserSummaryDTOResponse dto);
}
