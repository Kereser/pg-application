package co.com.crediya.application.api.helper;

import org.mapstruct.Mapper;

import co.com.crediya.application.api.dto.ApplicationFiltersDTORequest;
import co.com.crediya.application.api.dto.CreateApplicationDTORequest;
import co.com.crediya.application.model.dto.CreateApplicationCommand;
import co.com.crediya.application.model.dto.GetApplicationFilteredCommand;

@Mapper(componentModel = "spring")
public interface ApplicationRestMapper {

  CreateApplicationCommand toCommand(CreateApplicationDTORequest request);

  GetApplicationFilteredCommand toCommand(ApplicationFiltersDTORequest filters);
}
