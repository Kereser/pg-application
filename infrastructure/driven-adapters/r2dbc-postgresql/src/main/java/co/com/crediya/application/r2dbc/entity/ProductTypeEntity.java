package co.com.crediya.application.r2dbc.entity;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

@Table("product_type")
public record ProductTypeEntity(
    @Id @Column("product_type_id") UUID id,
    @NonNull String name,
    @NonNull @Column("interest_rate") BigDecimal interestRate,
    @Column("auto_validation") boolean autoValidation,
    @NonNull @Column("min_amount") BigDecimal minAmount,
    @NonNull @Column("max_amount") BigDecimal maxAmount) {}
