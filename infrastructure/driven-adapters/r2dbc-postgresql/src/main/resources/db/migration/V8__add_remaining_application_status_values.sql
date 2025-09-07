INSERT INTO application_status (application_status_id, name, description)
VALUES
(gen_random_uuid(), 'REJECTED', 'Applicant is not suitable for application'),
(gen_random_uuid(), 'APPROVED', 'Application is success'),
(gen_random_uuid(), 'MANUAL_REVISION', 'Application needs manual revision to set a definite value')