-- Add audit timestamps to pagamento table
-- Adds created_at and updated_at columns
ALTER TABLE pagamento
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;


