-- Add 'full_name' column to users table
ALTER TABLE users ADD COLUMN full_name VARCHAR(255);
ALTER TABLE users ADD COLUMN promotion VARCHAR(255);
ALTER TABLE users ADD COLUMN registration_number VARCHAR(255);
ALTER TABLE users ADD COLUMN speciality VARCHAR(255);
ALTER TABLE users ADD COLUMN department VARCHAR(255);
ALTER TABLE users ADD COLUMN grade VARCHAR(255);
ALTER TABLE users ADD COLUMN supervisor_id BIGINT(10);
