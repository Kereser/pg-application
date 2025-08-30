package co.com.crediya.application.r2dbc.producttype.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import co.com.crediya.application.model.producttype.ProductType;
import co.com.crediya.application.r2dbc.entity.ProductTypeEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductTypeMapperStandard {
  ProductType toEntity(ProductTypeEntity data);

  ProductTypeEntity toData(ProductType productType);
}
