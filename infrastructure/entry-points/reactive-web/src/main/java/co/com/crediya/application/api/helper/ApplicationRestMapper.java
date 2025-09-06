package co.com.crediya.application.api.helper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import co.com.crediya.application.api.dto.ApplicationFiltersDTORequest;
import co.com.crediya.application.api.dto.CreateApplicationDTORequest;
import co.com.crediya.application.model.CommonConstants;
import co.com.crediya.application.model.dto.CreateApplicationCommand;
import co.com.crediya.application.model.dto.GetApplicationFilteredCommand;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApplicationRestMapper {

  CreateApplicationCommand toCommand(CreateApplicationDTORequest request);

  @Mapping(target = CommonConstants.Mappers.FILTERS_PRODUCT_TYPE_IDS, ignore = true)
  GetApplicationFilteredCommand toCommand(ApplicationFiltersDTORequest filters);
}
