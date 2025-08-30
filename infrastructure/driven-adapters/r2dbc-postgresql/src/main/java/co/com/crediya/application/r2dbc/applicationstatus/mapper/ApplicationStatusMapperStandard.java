package co.com.crediya.application.r2dbc.applicationstatus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.r2dbc.entity.ApplicationStatusEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApplicationStatusMapperStandard {

  ApplicationStatus toEntity(ApplicationStatusEntity data);

  ApplicationStatusEntity toData(ApplicationStatus entity);
}
