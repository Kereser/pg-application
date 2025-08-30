package co.com.crediya.application.r2dbc.entity;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

@Table("applications")
public record ApplicationEntity(
    @Id @Column("application_id") UUID id,
    @NonNull @Column("user_id") UUID userId,
    @NonNull BigDecimal amount,
    @NonNull @Column("application_period") Integer applicationPeriod,
    @NonNull @Column("application_status_id") UUID applicationStatusId,
    @NonNull @Column("product_type_id") UUID productTypeId) {}
