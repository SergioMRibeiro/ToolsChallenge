-- Reduz tamanho do campo transacao_id para 15 caracteres
-- Necessário após padronização do identificador da transação

ALTER TABLE pagamento
ALTER COLUMN transacao_id TYPE VARCHAR(15);