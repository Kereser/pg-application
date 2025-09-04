package co.com.crediya.application.r2dbc.application.mapper;

import java.math.BigDecimal;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.application.ApplicationSummary;
import co.com.crediya.application.model.application.vo.Amount;
import co.com.crediya.application.model.application.vo.ApplicationPeriod;
import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.dto.ApplicationDTOResponse;
import co.com.crediya.application.model.dto.CreateApplicationCommand;
import co.com.crediya.application.model.mapper.ApplicationMapper;
import co.com.crediya.application.model.producttype.ProductType;
import co.com.crediya.application.r2dbc.entity.ApplicationEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApplicationMapperStandard extends ApplicationMapper {

  @Override
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userId", ignore = true)
  @Mapping(target = "applicationStatus", ignore = true)
  @Mapping(target = "productType", ignore = true)
  Application toEntityCommand(CreateApplicationCommand command);

  @Mapping(target = "id", source = "data.id")
  @Mapping(
      target = "applicationStatus",
      expression = "java(toApplicationStatus(data.applicationStatusId()))")
  @Mapping(target = "productType", expression = "java(toProductType(data.productTypeId()))")
  Application toEntity(ApplicationEntity data);

  @Mapping(target = "amount", source = "amount.value")
  @Mapping(target = "applicationPeriod", source = "applicationPeriod.value")
  @Mapping(target = "applicationStatusId", source = "applicationStatus.id")
  @Mapping(target = "productTypeId", source = "productType.id")
  ApplicationEntity toData(Application application);

  @Override
  @Mapping(target = "amount", source = "amount.value")
  @Mapping(target = "statusId", expression = "java(toStatusId(entity))")
  @Mapping(target = "productTypeId", expression = "java(toProductId(entity))")
  ApplicationDTOResponse toDTO(Application entity);

  @Mapping(target = "userId", source = "application.userId")
  @Mapping(target = "productType", source = "application.productType")
  @Mapping(target = "status", source = "application.applicationStatus")
  @Mapping(target = "amount", source = "application.amount.value")
  @Mapping(target = "applicationPeriod", source = "application.applicationPeriod.value")
  @Mapping(target = "interestRate", source = "application.productType.interestRate")
  @Mapping(target = "totalApprovedDebt", ignore = true)
  @Mapping(target = "name", source = "user.firstName")
  ApplicationSummary toSummary(Application application, UserSummary user);

  default UUID toStatusId(Application entity) {
    return entity.getApplicationStatus().getId();
  }

  default UUID toProductId(Application entity) {
    return entity.getProductType().getId();
  }

  default Amount toAmount(BigDecimal value) {
    return new Amount(value);
  }

  default ApplicationPeriod toApplicationPeriod(Integer value) {
    return new ApplicationPeriod(value);
  }

  default ApplicationStatus toApplicationStatus(UUID applicationStatusId) {
    return ApplicationStatus.builder().id(applicationStatusId).build();
  }

  default ProductType toProductType(UUID productTypeId) {
    return ProductType.builder().id(productTypeId).build();
  }
}
