INSERT INTO product_type (product_type_id, name, interest_rate, auto_validation, min_amount, max_amount)
VALUES
(gen_random_uuid(), 'VEHICLE_LOAN', 0.12, true, 50000000, 200000000),
(gen_random_uuid(), 'FREE_INVESTMENT_LOAN', 0.18, false, 10000000, 600000000)
