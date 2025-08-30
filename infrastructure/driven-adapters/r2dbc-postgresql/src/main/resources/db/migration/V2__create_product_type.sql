CREATE TABLE IF NOT EXISTS product_type (
    product_type_id UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    min_amount NUMERIC(19, 2) NOT NULL,
    max_amount NUMERIC(19, 2) NOT NULL,
    interest_rate NUMERIC(3, 2) NOT NULL,
    auto_validation BOOLEAN NOT NULL DEFAULT FALSE
);