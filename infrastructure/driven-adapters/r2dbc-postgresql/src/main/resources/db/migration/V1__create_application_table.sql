CREATE TABLE IF NOT EXISTS applications (
    application_id UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    application_period NUMERIC(2) NOT NULL,
    application_status_id UUID NOT NULL,
    product_type_id UUID NOT NULL
);