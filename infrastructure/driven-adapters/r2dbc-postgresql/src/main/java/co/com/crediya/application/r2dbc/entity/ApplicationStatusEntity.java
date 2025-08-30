package co.com.crediya.application.r2dbc.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

@Table("application_status")
public record ApplicationStatusEntity(
    @Id @Column("application_status_id") UUID id,
    @NonNull String name,
    @NonNull String description) {}
